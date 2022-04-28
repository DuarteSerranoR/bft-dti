package bft_dti;

import java.io.Console;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class BFT_DTIClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 3) ? Integer.parseInt(args[0]) : 1001;
        BFT_DTI bftMap = new BFT_DTI(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tMY_COINS: Returns all Coin information that you own.");
        System.out.println("\tMINT: Creates a Coin and returns the Coin Id. Takes in the Coin's value."); // Coins
        System.out.println("\tSPEND: Spends the value of specific coins (through their coinIds), creating an payment-coin for a receiver and the change-coin for the spender (if needed).");
        System.out.println("\tMY_NFTS: Returns all NFT information you own.");
        System.out.println("\tMINT_NFT: Creates a NFT with unique Id and URI. Returns the new NFT's generated Id.");
        System.out.println("\tREQUEST_NFT_TRANSFER: Submit a request to buy a NFT from someone. Specify what coins to use and an 'acceptance' expiration date.");
        System.out.println("\tCANCEL_REQUEST_NFT_TRANSFER: Cancel a submited request for a specific NFT.");
        System.out.println("\tMY_NFT_REQUESTS: List the requests done for a specific NFT.");
        System.out.println("\tPROCESS_NFT_TRANSFER: ...");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd == null) {
                break;
            } else if (cmd.equalsIgnoreCase("MY_COINS")) {

                Map<Long, Coin> myCoins = bftMap.getCoins();
                float total = 0;
                if(myCoins.size() > 0)
                    total = myCoins.values()
                                    .stream()
                                    .map(e -> e.Value)
                                    .reduce(Float::sum)
                                    .get()
                                    .floatValue();

                System.out.println("Your Coins: " + myCoins.entrySet());
                System.out.println("Total Value: " + total);

            } else if (cmd.equalsIgnoreCase("MINT")) { // Coins

                String valueStr = console.readLine("Enter Mint value: ");
                float value = Float.parseFloat(valueStr);
                long id = bftMap.Mint(value);
                if (id != -1)
                    System.out.println("Minted Coin with id " + id);

            } else if (cmd.equalsIgnoreCase("SPEND")) {

                /*
                *   SPEND(coins, receiver, value): require an array with the ids of the coins that will be
                *       used as input, the id of the user that will receive the transfer (receiver) and the value
                *       to be transferred. If the indicated coins provide enough funds to execute the
                *       transaction (sum(coins) >= value), the operation consumes the coins and generates
                *       two coins, one for the receiver with the value it received and another for the issuer
                *       with the remaining value (sum(coins)-value). The operation returns the id of the coin
                *       created for the issuer with the remaining value or 0 NOTE changed to -1 NOTE in case no coin was created (due
                *       to no remain or invalid operation).
                */
                
                String coinIdsStr = console.readLine("Enter your CoinIds input (long values separated by ','): ");
                coinIdsStr.replace(" ", "");
                long[] coinIds = Stream.of(coinIdsStr.split(",")).mapToLong(c -> Long.parseLong(c)).toArray();

                String receiverIdStr = console.readLine("Enter the ReceiverId: ");
                int receiverId = Integer.parseInt(receiverIdStr);
                
                String valueStr = console.readLine("Enter the Value to Spend: ");
                float value = Float.parseFloat(valueStr);

                Long leftoverCoinId = bftMap.Spend(coinIds, receiverId, value);
                if (leftoverCoinId != null && leftoverCoinId == -1)
                    System.out.println("Transaction finished. No change was generated since the value was on point.");
                else if (leftoverCoinId != null)
                    System.out.println("Transaction finished. Change created with the Coin Id: { " + leftoverCoinId.longValue() + " }.");

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {

                Map<Long, NFT> myNFTs = bftMap.getNFTs();

                System.out.println("Your NFTs: " + myNFTs.entrySet());

            } else if (cmd.equalsIgnoreCase("MINT_NFT")) {

                /*
                MINT_NFT(name, uri): create an NFT for the issuer with the name and uri specified.
                There cannot be two NFTs with the same name or the same. Returns the id of the
                newly created NFT.
                */

                String nameStr = console.readLine("Enter new NFT Name: ");
                String uriStr = console.readLine("Enter new NFT URI: ");
                Long id = bftMap.MintNFT(nameStr, uriStr);

                if (id != null && id != -1)
                    System.out.println("Minted NFT with id " + id);
                else if (id == null)
                    System.out.println("NFT with URI '" + uriStr + "' already existed.");

            } else if (cmd.equalsIgnoreCase("REQUEST_NFT_TRANSFER")) {
                
                /*
                REQUEST_NFT_TRANSFER(nft, coins, value, validity): create a request transfer for nft
                with an offered value and confirmation validity. Each user can create at most one
                purchase offer per NFT.
                */
                
                // Validity or expiration date??? Went with validity on the sense of Expiration date
                long nftId = Long.parseLong(console.readLine("Enter the target NFT's Id: "));
                String coinIdsStr = console.readLine("Enter your CoinIds input (long values separated by ','): ");
                coinIdsStr.replace(" ", "");
                long[] coinIds = Stream.of(coinIdsStr.split(",")).mapToLong(c -> Long.parseLong(c)).toArray();
                String valueStr = console.readLine("Enter the Value to Spend: ");
                float value = Float.parseFloat(valueStr);
                String validityStr = console.readLine("Enter the Validity/Expiration date of your request (yyyy-MM-ddThh:mm:ss.kk->2018-12-30T19:34:50.63): ");
                LocalDateTime validity = LocalDateTime.parse(validityStr);

                boolean transfSuccess = bftMap.RequestNFTTransfer(nftId, coinIds, value, validity);

                if (transfSuccess)
                    System.out.println("NFT Transfer request successfuly submited.");
                else
                    System.out.println("NFT Transfer request submission failed.");

            } else if (cmd.equalsIgnoreCase("CANCEL_REQUEST_NFT_TRANSFER")) {
                
                /*
                CANCEL_REQUEST_NFT_TRANSFER(nft): cancel a previously created transfer request
                for nft. If the user did not create such request, the operation does nothing.
                */
                
                long nftId = Long.parseLong(console.readLine("Enter the Request's NFT Id: "));
                bftMap.CancelNFTTransferRequest(nftId);
            } else if (cmd.equalsIgnoreCase("MY_NFT_REQUESTS")) {
                
                /*
                MY_NFT_REQUESTS(nft): lists the purchase offers (issuer, value and validity) for such
                NFT if the caller is the owner of the NFT.
                */
                
                long nftId = Long.parseLong(console.readLine("Enter the NFT Id to get it's Requests: "));
                Set<Long> myNFTs = bftMap.getNFTs().keySet();
                if (myNFTs.stream().anyMatch(n -> n.longValue() == nftId)) {
                    Set<NFTRequest> requests = bftMap.MyNFTRequests(nftId);
                    if (requests != null)
                        System.out.println("NFT Transfer request for NFT '" + nftId + "': " + requests + ".");
                } else {
                    System.out.println("NFT not owned.");
                }

            } else if (cmd.equalsIgnoreCase("PROCESS_NFT_TRANSFER")) {
                
                /*
                PROCESS_NFT_TRANSFER(nft, buyer, accept): when called by the owner of nft, it can
                accept or reject the NFT transfer by using true or false on the accept parameter. If the
                buyer has not created an offer to the NFT or if the request expired, nothing happens.
                Otherwise, in case of acceptance, if the buyer coins are still available, use them to pay
                the value (creating two coins, just like in SPEND) and change the ownership of the NFT
                to the buyer. Return the id of the new coin created for the issuer with the payment or
                0 if the operation is not performed.
                */

                // TODO did not confirm expiration

                long nftId = Long.parseLong(console.readLine("Enter the NFT's Id: "));
                int buyer = Integer.parseInt(console.readLine("Enter the Buyer (NFTRequest.From) Id: "));
                boolean accept = Boolean.parseBoolean(console.readLine("Do you accept the transcation? (true/false): "));

                long outCoinId = bftMap.ProcessNFTTransfer(nftId, buyer, accept);
                if (outCoinId != -1)
                    System.out.println("Transaction finished. Coin: " + outCoinId);
            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
