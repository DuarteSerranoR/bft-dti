package bft_dti;

// import java.util.Set;
import java.io.Console;
import java.io.IOException;

public class BFT_DTIClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 3) ? Integer.parseInt(args[0]) : 1001;
        BFT_DTI bftMap = new BFT_DTI(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tMY_COINS: ...");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("MY_COINS")) {

                // int key;
                /*
                 * try {
                 * key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                 * } catch (NumberFormatException e) {
                 * System.out.println("\tThe key is supposed to be an integer!\n");
                 * continue;
                 * }
                 */

                // String value = console.readLine("Enter an alpha-numeric value: ");

                // invokes the op on the servers
                bftMap.getCoins(clientId);

                System.out.println("\nkey-value pair added to the map\n");
            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
