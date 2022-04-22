package bft_dti;

import java.io.Console;
import java.io.IOException;
import java.util.Map;

public class BFT_DTIClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 3) ? Integer.parseInt(args[0]) : 1001;
        BFT_DTI bftMap = new BFT_DTI(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tMY_COINS: ...");
        System.out.println("\tMINT: ..."); // Coins

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd == null) {
                break;
            } else if (cmd.equalsIgnoreCase("MY_COINS")) {
                Map<Long, Coin> myCoins = bftMap.getCoins();
                System.out.println("Your coins: " + myCoins.entrySet());
            } else if (cmd.equalsIgnoreCase("MINT")) { // Coins
                long id = bftMap.Mint(Float.parseFloat(console.readLine("Enter Mint value: ")));
                System.out.println("Minted coin with id " + id);
            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
