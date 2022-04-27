package bft_dti;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class NFTRequest implements Serializable, Comparable<NFTRequest> {
    
    @Serial
    long NFT; // Serves as to owner

    @Serial
    long[] Coins; // Serves as from owner

    @Serial
    float Value;

    @Serial
    LocalDateTime Validity;

    @Serial
    int From; // still needed for uniqueness

    public NFTRequest(long nft, long[] coins, float value, LocalDateTime validity, int from) {
        NFT = nft;
        Coins = coins;
        Value = value;
        Validity = validity;
        From = from;
    }

    @Override
    public String toString() {
        return "NFTRequest: { NFT: " + NFT + "; Coins: " + Coins + "; Value: '" + Value + "'; Validity: '" + Validity + "'; From: '" + From + "'; }";
    }
 
    @Override
    public int compareTo(NFTRequest obj) {
        if (this.NFT < obj.NFT)
            return -1;
        if (this.NFT == obj.NFT)
            return 0;

        return 1;
    }

}
