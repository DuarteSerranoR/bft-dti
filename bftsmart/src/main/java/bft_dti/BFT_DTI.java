package bft_dti;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bftsmart.tom.ServiceProxy;

public class BFT_DTI {
    int Id;
    private final BFTCoinMap CoinsMap;
    private final BFTNftMap NFTsMap;

    public BFT_DTI(int id) {
        Id = id;
        ServiceProxy sProxy = new ServiceProxy(id);
        Logger logger = LoggerFactory.getLogger("bft_dti");
        CoinsMap = new BFTCoinMap(sProxy, logger);
        NFTsMap = new BFTNftMap(sProxy, logger);
    }

    // Using TreeSets to store the ids

    // Coins
    public Map<Long, Coin> getCoins() {
        return CoinsMap.ClientEntryMap();
    }

    public long Mint(float value) {
        /* NOTE -> Can't just send the entire request, since
        *          we are generating a completely random CoinIds.
        *          The different servers could generate different
        *          ids, ruining consensus. So we get the current coin
        *          ids, and generate a unique and completely random 
        *          coin Id with them.
        */
        long[] coinIds = CoinsMap.KeySet()
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .toArray();
        try {
            Coin c = new Coin(coinIds, Id, value);
            CoinsMap.Put(c.Id, c);
            return c.Id;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    public Long Spend(long[] usedCoinIds, int receiverId, float value) {

        try {
            long[] coinIds = CoinsMap.KeySet()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .mapToLong(Long::longValue)
                                    .toArray();
            
            long changeId = Utils.GenerateUniqueId(coinIds);
            ArrayList<Long> coinIdsAndChange = new ArrayList<Long>();
            for (long id : coinIds) {
                coinIdsAndChange.add(id);
            }
            coinIdsAndChange.add(changeId);
            coinIds = coinIdsAndChange.stream().mapToLong(id -> id.longValue()).toArray();
    
            long valueId = Utils.GenerateUniqueId(coinIds);
    
            return CoinsMap.Spend(usedCoinIds, receiverId, Id, value, valueId, changeId);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    // NFTs
    public Map<Long, NFT> getNFTs() {
        return NFTsMap.ClientEntryMap();
    }
    
    public Long MintNFT(String name, String uri) {

        Map<Long, String> nftKeyMap = NFTsMap.KeyMap();

        if (nftKeyMap.values().contains(uri))
            return null;

        long[] nftIds = nftKeyMap.keySet()
                                .stream()
                                .filter(Objects::nonNull)
                                .mapToLong(Long::longValue)
                                .toArray();

        try {
            long id = Utils.GenerateUniqueId(nftIds);
            NFT n = new NFT(id, Id, name, uri);
            return NFTsMap.Put(id, n);
        } catch (Exception e) {
            System.out.println(e);
            return Long.valueOf(-1);
        }
    }
    
    // NFT Transfers
    public boolean RequestNFTTransfer(long nftId, long[] coinsIds, float value, LocalDateTime validity) {
        NFTRequest request = new NFTRequest(nftId, coinsIds, value, validity, Id);
        Boolean success = NFTsMap.RequestNFTTransfer(request);
        if (success == null)
            success = Boolean.valueOf(false);
        return success;
    }
    
    public void CancelNFTTransferRequest(long nftId) {
        NFTsMap.CancelNFTTransferRequest(nftId, Id);
    }
    
    public Set<NFTRequest> MyNFTRequests(long nftId) {
        return NFTsMap.RequestsSet(nftId);
    }

    public long ProcessNFTTransfer(long nftId, int buyer, boolean accept) {
        if (accept) {
            Long change = NFTsMap.ProcessNFTTransfer(nftId, buyer);
            if (change != null) return change.longValue();
            else return -1;
        }
        else return -1;
    }
}
