package bft_dti;

import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.stream.LongStream;

public class Utils {

    //////////////////////////////////////////////////////////////////////////////
    /// Generates a completely random long value,
    /// having takes an array of long values to ignore.
    ///
    /// If the array is filled up, this method could take a
    /// while and throw an exception to avoid creating non ending
    /// loops.
    ///
    /// Takes in a configurable timeout in miliseconds.
    //////////////////////////////////////////////////////////////////////////////
    public static long GenerateUniqueId(long[] ids, long timeout) throws TimeoutException {
        long start = System.currentTimeMillis();

        Random rand = new Random();
        long end = start + timeout;

        while (true) {
            //long id = rand.nextLong(0, 1_000_000); // 1M values
            long id = rand.nextLong(); // 1M values
            // if (id < 0)
            // id = -id;
            if (!LongStream.of(ids).anyMatch(x -> x == id) && id > 0) {
                return id;
            }
            if (System.currentTimeMillis() >= end) {
                throw new TimeoutException("Timed out generating an unique Id from ids array.");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    /// Generates a completely random long value,
    /// having takes an array of long values to ignore.
    ///
    /// If the array is filled up, this method could take a
    /// while and throw an exception to avoid creating non ending
    /// loops.
    ///
    /// Default timeout = 10 000 miliseconds.
    //////////////////////////////////////////////////////////////////////////////
    public static long GenerateUniqueId(long[] ids) throws TimeoutException {
        long timeout = 10000; // Ms
        return GenerateUniqueId(ids, timeout);
    }
}
