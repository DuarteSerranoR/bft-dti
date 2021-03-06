package bft_dti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
//import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceReplica replica;

    //@Serial
    private TreeMap<Long, Coin> coinMap;
    //@Serial
    private TreeMap<Long, NFT> nftMap;
    //@Serial
    private TreeSet<NFTRequest> nftRequestsSet;

    // The constructor passes the id of the server to the super class
    public BFTMapServer(int id) {
        coinMap = new TreeMap<Long, Coin>();
        nftMap = new TreeMap<Long, NFT>();
        nftRequestsSet = new TreeSet<NFTRequest>();
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

            logger.info("Ordered execution of a {} request from {}", cmd,
                    msgCtx.getSender());

            switch (cmd) { 
                
                // NOTE - a few questions I have are -> Wouldn't the use of grpc represent a good way to send binary data safely and securely throughout the servers? Or would it hurt the protocol's performance too much?
                // NOTE -                            -> Wouldn't there be a way to store the status of these objects in WAL Logs? Or even append to disk, trying to only
                //                                      close the application with SIGINT like some kind of Redis DB? Since they need to be serializable.

                case PUT_COIN:
                    Long key = (Long) objIn.readObject();
                    Coin value = (Coin) objIn.readObject();

                    Coin ret = coinMap.putIfAbsent(key, value);

                    if (ret == null) {
                        objOut.writeObject(value);
                        reply = byteOut.toByteArray();
                    }
                    break;

                case COIN_KEYSET:
                    Set<Long> kSet = coinMap.keySet().stream().parallel()
                            .map(k -> Long.parseLong(k.toString()))
                            .collect(Collectors.toSet());

                    if (kSet != null) {
                        objOut.writeObject(kSet);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case COIN_USERMAP:
                    Set<Entry<Long, Coin>> esetCoin = coinMap.entrySet();

                    if (esetCoin != null) {

                        Map<Long, Coin> es = esetCoin.stream()
                                .filter(e -> e.getValue().Owner == msgCtx.getSender())
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                        objOut.writeObject(es);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case NFT_USERMAP:
                    Set<Entry<Long, NFT>> esetNft = nftMap.entrySet(); // Set was not returning ordered, ruining the
                                                                        // consensus of our application. So I converted it
                                                                        // into a returnable Map.
                                                                        // Later learned of treeset, not enough time to fix it.

                    if (esetNft != null) {

                        Map<Long, NFT> es = esetNft.stream()
                                .filter(e -> e.getValue().Owner == msgCtx.getSender())
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

                        objOut.writeObject(es);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case NFT_KEYMAP:
                    Set<Entry<Long, NFT>> Nfts = nftMap.entrySet();

                    if (Nfts != null) {

                        Map<Long, String> es = Nfts.stream()
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().URI));

                        objOut.writeObject(es);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case SPEND_COINS:
                    long[] coinIds = (long[]) objIn.readObject();
                    Integer receiverId = (Integer) objIn.readObject();
                    float valueSpent = (float) objIn.readObject();
                    int senderId = (int) objIn.readObject();

                    ArrayList<Long> coinIdsList = new ArrayList<Long>();
                    for (long id : coinIds) {
                        coinIdsList.add(id);
                    }
                    Optional<Float> _coinsTotalValue = coinMap.entrySet()
                                                    .stream()
                                                    .filter(e -> coinIdsList.contains(e.getKey()))
                                                    .map(e -> e.getValue().Value)
                                                    .reduce(Float::sum);
                    
                    if (_coinsTotalValue.isPresent()) {
                        
                        float coinsTotalValue = _coinsTotalValue.get().floatValue();

                        if (coinsTotalValue >= valueSpent) {

                            coinMap.keySet().removeAll(coinIdsList);

                            float changeValue = coinsTotalValue - valueSpent;
    
                            long spentValId = (long) objIn.readObject();
                            Coin receivedCoin = new Coin(spentValId, receiverId, valueSpent);
                            coinMap.put(spentValId, receivedCoin);

                            long changeId = (long) objIn.readObject();
                            if (changeValue != 0) {
                                Coin change = new Coin(changeId, senderId, changeValue);
                                coinMap.put(changeId, change);
                            } else {
                                changeId = -1;
                            }
    
                            objOut.writeObject("Operation successfuly executed");
                            objOut.writeObject(changeId);
                        }
                        else {
                            objOut.writeObject("Not enough");
                            objOut.writeObject(coinsTotalValue);
                        }
                    } else {
                        objOut.writeObject("Coins not found.");
                        logger.error("Coins not found.");
                    }
                    reply = byteOut.toByteArray();

                    break;

                case PUT_NFT:
                    Long nKey = (Long) objIn.readObject();
                    NFT nValue = (NFT) objIn.readObject();

                    if (nftMap.values().stream().anyMatch(n -> n.URI == nValue.URI))
                        break;
                        
                    NFT k = nftMap.putIfAbsent(nKey, nValue);

                    if (k == null) {
                        objOut.writeObject(nKey);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case PUT_NFT_REQUEST:
                    NFTRequest nRequest = (NFTRequest) objIn.readObject();

                    if (nftRequestsSet.stream().anyMatch(n -> n.From == nRequest.From && n.NFT == nRequest.NFT)) {
                        objOut.writeObject(false);
                        reply = byteOut.toByteArray();
                        break;
                    }
                        
                    boolean r = nftRequestsSet.add(nRequest);
                    
                    objOut.writeObject(r);
                    reply = byteOut.toByteArray();

                    break;

                case REMOVE_NFT_REQUEST:
                    Long nftId = (Long) objIn.readObject();
                    Integer clientId = (Integer) objIn.readObject();

                    NFTRequest reqs = nftRequestsSet.stream().filter(e -> e.NFT == nftId && e.From == clientId).findAny().orElse(null);

                    if (reqs != null) {
                        nftRequestsSet.remove(reqs);
                        objOut.writeObject(true);
                        reply = byteOut.toByteArray();
                    } else {
                        objOut.writeObject(false);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case NFT_REQUESTS:
                    Long tNftId = (Long) objIn.readObject();

                    TreeSet<NFTRequest> optnftReq = nftRequestsSet.stream()
                                                    .filter(e -> e.NFT == tNftId)
                                                    .collect(Collectors.toCollection(TreeSet::new));

                    if (optnftReq != null) {
                        objOut.writeObject(optnftReq);
                        reply = byteOut.toByteArray();
                    }

                    break;

                case TRANSFER_NFT:
                    Long targetNftId = (Long) objIn.readObject();
                    Integer buyer = (Integer) objIn.readObject();

                    NFTRequest targetRequest = nftRequestsSet.stream()
                                                    .filter(e -> e.NFT == targetNftId.longValue() && e.From == buyer.intValue())
                                                    .findAny()
                                                    .orElse(null);
                                                    
                    if (targetRequest != null) {
                        // Changed owner
                        NFT targetNFT = nftMap.get(targetNftId);
                        Integer owner = Integer.valueOf(targetNFT.Owner);
                        targetNFT.Owner = buyer.intValue(); 
                        nftMap.put(targetNFT.Id, targetNFT);

                        // Transaction
                        long[] targetCoinIds = targetRequest.Coins;
                        float valu = targetRequest.Value;
                        ArrayList<Long> targetCoinIdsList = new ArrayList<Long>();
                        for (long id : targetCoinIdsList) {
                            targetCoinIdsList.add(id);
                        }
                        Optional<Float> _targetCoinsTotalValue = coinMap.entrySet()
                                                        .stream()
                                                        .filter(e -> targetCoinIdsList.contains(e.getKey()))
                                                        .map(e -> e.getValue().Value)
                                                        .reduce(Float::sum);
                        if (_targetCoinsTotalValue.isPresent()) {
                                                
                            float targetCoinsTotalValue = _targetCoinsTotalValue.get().floatValue();

                            if (targetCoinsTotalValue >= valu) {

                                coinMap.keySet().removeAll(targetCoinIdsList);

                                float changeValue = targetCoinsTotalValue - valu;
        
                                long spentValId = (long) objIn.readObject();
                                Coin receivedCoin = new Coin(spentValId, owner.intValue(), valu);
                                coinMap.put(spentValId, receivedCoin);

                                long changeId = (long) objIn.readObject();
                                if (changeValue != 0) {
                                    Coin change = new Coin(changeId, buyer.intValue(), changeValue);
                                    coinMap.put(changeId, change);
                                }
        
                                objOut.writeObject("Operation successfuly executed");
                                objOut.writeObject(receivedCoin.Id);
                            }
                            else {
                                objOut.writeObject("Not enough");
                                objOut.writeObject(-1);
                            }
                        } else {
                            objOut.writeObject("Coins not found.");
                            logger.error("Coins not found.");
                            objOut.writeObject(-1);
                        }
                        reply = byteOut.toByteArray();

                        // Remove Request
                        nftRequestsSet.remove(targetRequest);
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
                case PUT_COIN:
                    Long key = (Long) objIn.readObject();
                    Coin value = (Coin) objIn.readObject();

                    Coin ret = coinMap.putIfAbsent(key, value);

                    if (ret == null) {
                        objOut.writeObject(value);
                        reply = byteOut.toByteArray();
                    }
                    break;

                case PUT_NFT:
                    Long nKey = (Long) objIn.readObject();
                    NFT nValue = (NFT) objIn.readObject();

                    if (nftMap.values().stream().anyMatch(n -> n.URI == nValue.URI))
                        break;
                        
                    NFT k = nftMap.putIfAbsent(nKey, nValue);

                    if (k == null) {
                        objOut.writeObject(nKey);
                        reply = byteOut.toByteArray();
                    }

                break;

                case PUT_NFT_REQUEST:
                    NFTRequest nRequest = (NFTRequest) objIn.readObject();

                    if (nftRequestsSet.stream().anyMatch(n -> n.From == nRequest.From && n.NFT == nRequest.NFT)) {
                        objOut.writeObject(false);
                        reply = byteOut.toByteArray();
                        break;
                    }
                    
                    boolean r = nftRequestsSet.add(nRequest);
                    
                    objOut.writeObject(r);
                    reply = byteOut.toByteArray();

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
            Object[] maps = { coinMap, nftMap, nftRequestsSet };
            out.writeObject(maps);
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
            Object[] maps = (Object[]) in.readObject();
            coinMap = (TreeMap<Long, Coin>) maps[0];
            nftMap = (TreeMap<Long, NFT>) maps[1];
            nftRequestsSet = (TreeSet<NFTRequest>) maps[2];
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace(); // debug instruction
        }
    }
}