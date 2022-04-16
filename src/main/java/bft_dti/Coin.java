package bft_dti;

public class Coin {
    long Id; // there cannot be two coins with the same id
    int Owner;
    float Value;

    /// Coin object with unique id
    public Coin(long[] ids, int owner, float value) {
        Id = Utils.GenerateUniqueId(ids);
        Owner = owner;
        Value = value;
    }

    /// NFT object with any inputed id
    public Coin(long id, int owner, float value) {
        Id = id;
        Owner = owner;
        Value = value;
    }
}
