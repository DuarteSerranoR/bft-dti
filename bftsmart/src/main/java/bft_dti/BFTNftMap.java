package bft_dti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;

import bftsmart.tom.ServiceProxy;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

public class BFTNftMap {
    private final Logger logger;
    private final ServiceProxy serviceProxy;

    public BFTNftMap(ServiceProxy sProxy, Logger l) {
        logger = l;
        serviceProxy = sProxy;
    }
    
    /*
    public NFT Put(Long key, Coin value) {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(BFTMapRequestType.PUT_NFT);
            objOut.writeObject(key);
            objOut.writeObject(value);

            objOut.flush();
            byteOut.flush();

            rep = serviceProxy.invokeUnordered(byteOut.toByteArray());
        } catch (IOException e) {
            logger.error("Failed to send PUT request");
            return null;
        }
        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (Coin) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of PUT request");
            return null;
        }
    }

    public Set<Long> KeySet() {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.COIN_KEYSET);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialize response of KEYSET request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (Set<Long>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of KEYSET request");
            return null;
        }
    }
    */
    public Map<Long, NFT> ClientEntryMap() {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.NFT_USERMAP);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialized response of ENTRYSET request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (Map<Long, NFT>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of ENTRYSET request");
            return null;
        }
    }

    /*
    public Long Spend(long[] coinIds, int receiverId, int userId, float value, long spentId, long changeId) {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.SPEND_COINS);
            objOut.writeObject(coinIds);
            objOut.writeObject(Integer.valueOf(receiverId));
            objOut.writeObject(value);
            objOut.writeObject(Integer.valueOf(userId));
            objOut.writeObject(spentId);
            objOut.writeObject(changeId);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialized response of SPEND_COINS request");
            return null;
        }

        if (rep.length == 0) {
            System.out.println("Nothing returned");
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            
            String status = (String) objIn.readObject();
            if (status.equals((String) "Operation successfuly executed")) {
                return (long) objIn.readObject();
            }
            else if (status.equals((String) "Not enough")) {
                float currencyUsed = (float) objIn.readObject();
                System.out.println("Not enough to perform operation.");
                System.out.println("Currency in chosen coins: " + currencyUsed + "; Value needed: " + value + "; What is missing: " + (value - currencyUsed) + ".");
                return null;
            }
            else {
                System.out.println(status);
                return null;
            }
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of SPEND_COINS request");
            return null;
        }
    }
    */
}
