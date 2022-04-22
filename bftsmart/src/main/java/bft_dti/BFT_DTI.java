package bft_dti;

import java.util.Map;
import java.util.Objects;

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
        return CoinsMap.clientEntryMap();
    }

    public long Mint(float value) {
        long[] coinIds = CoinsMap.keySet()
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .toArray();
        try {
            Coin c = new Coin(coinIds, Id, value);
            CoinsMap.put(c.Id, c);
            return c.Id;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    public long Spend(long[] ids /* coins ids */, long id /* user id */) {
        // return ;
        return 0; // TODO
    }

    // NFTs

}
