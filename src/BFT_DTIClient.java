package bft_dti;

import java.io.Console;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BFT_DTIClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 3) ? Integer.parseInt(args[0]) : 1001;
        BFT_DTI bftMap = new BFT_DTI(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tMY_COINS: Returns all Coin information that you own.");
        System.out.println("\tMINT: Creates a Coin and returns the Coin Id. Takes in the Coin's value."); // Coins
        System.out.println("\tSPEND: ...");
        System.out.println("\tMY_NFTS: ...");
        System.out.println("\tMINT_NFT: ...");
        System.out.println("\tREQUEST_NFT_TRANSFER: ...");
        System.out.println("\tCANCEL_REQUEST_NFT_TRANSFER: ...");
        System.out.println("\tMY_NFT_REQUESTS: ...");
        System.out.println("\tPROCESS_NFT_TRANSFER: ...");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd == null) {
                break;
            } else if (cmd.equalsIgnoreCase("MY_COINS")) {

                Map<Long, Coin> myCoins = bftMap.getCoins();
                float total = 0;
                if(myCoins.size() > 0)
                    total = myCoins.values().stream().map(e -> e.Value).reduce(Float::sum).get().floatValue();

                System.out.println("Your Coins: " + myCoins.entrySet());
                System.out.println("Total Value: " + total);

            } else if (cmd.equalsIgnoreCase("MINT")) { // Coins

                long id = bftMap.Mint(Float.parseFloat(console.readLine("Enter Mint value: ")));
                System.out.println("Minted coin with id " + id);

            } else if (cmd.equalsIgnoreCase("SPEND")) {

                System.out.println("TODO");

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {
                System.out.println("TODO");
            } else if (cmd.equalsIgnoreCase("MINT_NFT")) {
                System.out.println("TODO");
            } else if (cmd.equalsIgnoreCase("REQUEST_NFT_TRANSFER")) {
                System.out.println("TODO");
            } else if (cmd.equalsIgnoreCase("CANCEL_REQUEST_NFT_TRANSFER")) {
                System.out.println("TODO");
            } else if (cmd.equalsIgnoreCase("MY_NFT_REQUESTS")) {
                System.out.println("TODO");
            } else if (cmd.equalsIgnoreCase("PROCESS_NFT_TRANSFER")) {
                System.out.println("TODO");
            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
