package bft_dti;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceReplica replica;
    private TreeMap<K, V> replicaMap;

    // The constructor passes the id of the server to the super class
    public BFTMapServer(int id) {
        replicaMap = new TreeMap<>();
        replica = new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BFTMapServer <server id>");
            System.exit(-1);
        }
        new BFTMapServer<Integer, String>(Integer.parseInt(args[0]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
                ObjectInputStream objIn = new ObjectInputStream(byteIn);
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            byte[] reply = new byte[0];
            BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();

            logger.info("Ordered execution of a {} request from {}", cmd, msgCtx.getSender());

            switch (cmd) {
                // write operations on the map
                case PUT:
                    K key = (K) objIn.readObject();
                    V value = (V) objIn.readObject();

                    V ret = replicaMap.put(key, value);

                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    break;
            }

            objOut.flush();
            byteOut.flush();

            return reply;
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to process ordered request", ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
                ObjectInputStream objIn = new ObjectInputStream(byteIn);
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {

            byte[] reply = new byte[0];
            BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();

            logger.info("Unordered execution of a {} request from {}", cmd, msgCtx.getSender());

            switch (cmd) {
                // read operations on the map
                case GET:
                    K key = (K) objIn.readObject();

                    V ret = replicaMap.get(key);

                    if (ret != null) {
                        objOut.writeObject(ret);
                        reply = byteOut.toByteArray();
                    }
                    break;
                case SIZE:
                    int n = replicaMap.size();

                    if (n != 0) {
                        objOut.writeObject(n);
                        reply = byteOut.toByteArray();
                    }
                    break;
                case REMOVE:
                    K keyR = (K) objIn.readObject();

                    V rem = replicaMap.remove(keyR);

                    if (rem != null) {
                        objOut.writeObject(rem);
                        reply = byteOut.toByteArray();
                    }
                    break;
                case KEYSET:
                    Set<Integer> kSet = replicaMap.keySet().stream()
                            .map(k -> Integer.parseInt(k.toString()))
                            .collect(Collectors.toSet());

                    if (kSet != null) {
                        objOut.writeObject(kSet);
                        reply = byteOut.toByteArray();
                    }

                    break;
            }

            objOut.flush();
            byteOut.flush();

            return reply;
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to process unordered request", ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(replicaMap);
            out.flush();
            bos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace(); // debug instruction
            return new byte[0];
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(state);
                ObjectInput in = new ObjectInputStream(bis)) {
            replicaMap = (TreeMap<K, V>) in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace(); // debug instruction
        }
    }
}