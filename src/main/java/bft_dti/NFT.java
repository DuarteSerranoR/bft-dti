package bft_dti;

import java.util.concurrent.TimeoutException;

public class NFT {
    long Id; // there cannot be two NFTs with the same id
    int Owner;
    String Name;
    String URI; // URI = URL

    /// NFT object with unique id
    public NFT(long[] ids, int owner, String name, String uri) throws TimeoutException {
        Id = Utils.GenerateUniqueId(ids);
        Owner = owner;
        Name = name;
        URI = uri;
    }

    /// NFT object with any inputed id
    public NFT(long id, int owner, String name, String uri) {
        Id = id;
        Owner = owner;
        Name = name;
        URI = uri;
    }
}
