package bft_dti;

import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BFT_DTI {
    private final BFTMap<Long, Coin> CoinsMap;
    private final BFTMap<Long, NFT> NFTsMap;

    public BFT_DTI(int id) {
        CoinsMap = new BFTMap(id);
        NFTsMap = new BFTMap(id);
    }

    // Using TreeSets to store the ids

    // Coins
    public Set<Entry<Long, Coin>> getCoins(int id) {
        Stream<Entry<Long, Coin>> s = CoinsMap.entrySet().stream().filter(e -> e.getValue().Owner == id);
        return s.collect(Collectors.toSet());
    }

    public long Mint(Coin c) {
        return 0; // TODO
    }

    public long Spend(long[] ids /* coins ids */, long id /* user id */) {
        // return ;
        return 0; // TODO
    }

    // NFTs

}
