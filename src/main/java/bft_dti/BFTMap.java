package bft_dti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;

import bftsmart.tom.ServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

public class BFTMap<K, V> implements Map<K, V> {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceProxy serviceProxy;

    public BFTMap(int id) {
        serviceProxy = new ServiceProxy(id);
    }

    /**
     *
     * @param key The key associated to the value
     * @return value The value previously added to the map
     */
    @Override
    public V get(Object key) {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.GET);
            objOut.writeObject(key);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(byteOut.toByteArray());
        } catch (IOException e) {
            logger.error("Failed to send GET request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (V) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of GET request");
            return null;
        }
    }

    /**
     *
     * @param key   The key associated to the value
     * @param value Value to be added to the map
     */
    @Override
    public V put(K key, V value) {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
            objOut.writeObject(BFTMapRequestType.PUT);
            objOut.writeObject(key);
            objOut.writeObject(value);

            objOut.flush();
            byteOut.flush();

            rep = serviceProxy.invokeOrdered(byteOut.toByteArray());
        } catch (IOException e) {
            logger.error("Failed to send PUT request");
            return null;
        }
        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (V) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of PUT request");
            return null;
        }
    }

    @Override
    public int size() {
        // throw new UnsupportedOperationException("You are supposed to implement this
        // method :)");
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.SIZE);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialized response of SIZE request");
            return 0;
        }

        if (rep.length == 0) {
            return 0;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (int) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SIZE request");
            return 0;
        }
    }

    @Override
    public V remove(Object key) {
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.REMOVE);
            objOut.writeObject(key);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(byteOut.toByteArray());
        } catch (IOException e) {
            logger.error("Failed to send REMOVE request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (V) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of REMOVE request");
            return null;
        }
    }

    @Override
    public Set<K> keySet() {
        // throw new UnsupportedOperationException("You are supposed to implement this
        // method :)");
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.KEYSET);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeUnordered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialized response of KEYSET request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (Set<K>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of KEYSET request");
            return null;
        }
    }

    @Override
    public Collection<V> values() {
        // throw new UnsupportedOperationException("You are supposed to implement this
        // method :)");
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.VALUES);

            objOut.flush();
            byteOut.flush();

            // invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(byteOut.toByteArray());
        } catch (IOException ex) {
            logger.error("Failed to deserialized response of VALUES request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
                ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
            return (Collection<V>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of VALUES request");
            return null;
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        // throw new UnsupportedOperationException("You are supposed to implement this
        // method :)");
        byte[] rep;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(BFTMapRequestType.ENTRYSET);

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
            return (Set<Entry<K, V>>) objIn.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of ENTRYSET request");
            return null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }
}
