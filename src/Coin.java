package bft_dti;

//import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

public class Coin implements Serializable {

    //@Serial
    long Id; // there cannot be two coins with the same id

    //@Serial
    int Owner;

    //@Serial
    float Value;

    /// Coin object with unique id
    public Coin(long[] ids, int owner, float value) throws TimeoutException {
        Id = Utils.GenerateUniqueId(ids);
        Owner = owner;
        Value = value;
    }

    /// Coin object with any inputed id
    public Coin(long id, int owner, float value) {
        Id = id;
        Owner = owner;
        Value = value;
    }

    @Override
    public String toString() {
        return "Coin: { Id: " + Id + "; Owner: " + Owner + "; Value: " + Value + "; }";
    }
}
