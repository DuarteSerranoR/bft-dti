import java.io.Serial;

import bft_dti.NFT;

public class NFTRequest implements Serializable {
    
    @Serial
    long NFT;

    @Serial
    long[] Coins;

    @Serial
    float Value;

    @Serial
    boolean Validity;

    public NFTRequest(long nft, long[] coins, float value, boolean validity) {
        NFT = nft;
        Coins = coins;
        Value = value;
        Validity = validity;
    }

}
