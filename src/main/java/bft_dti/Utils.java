package bft_dti;

import java.util.Random;
import java.util.stream.LongStream;

public class Utils {

    //////////////////////////////////////////////////////////////////////////////
    /// Generates a completely random long value,
    /// having takes an array of long values to ignore.
    ///
    /// If the array is filled up, this method could take a
    /// while and throw an exception to avoid creating non ending
    /// loops.
    //////////////////////////////////////////////////////////////////////////////
    public static long GenerateUniqueId(long[] ids) {
        Random rand = new Random();
        while (true) {
            long id = rand.nextLong();
            if (!LongStream.of(ids).anyMatch(x -> x == id)) {
                return id;
            }
        }
    }
}
