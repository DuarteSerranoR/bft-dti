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
    
    public Long Put(Long key, NFT value) {
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
            return (Long) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of PUT request");
            return null;
        }
    }
    
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

    public Map<Long, String> KeyMap() { // String represents the unique Uri
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.NFT_KEYMAP);

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
            return (Map<Long, String>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialize response of ENTRYSET request");
            return null;
        }
    }
}
