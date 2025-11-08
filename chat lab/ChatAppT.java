package app;
import core.Server;
import core.Client;
public class ChatAppT {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  As Client: java App -c <username> <server_ip>");
            System.out.println("  As Server: java App -s <username>");
            return;
        }

        String flag = args[0];

        switch (flag) {
            case "-c": // Client mode
                if (args.length < 3) {
                    System.out.println("Client mode requires: <username> <server_ip>");
                    return;
                }
                String clientUsername = args[1];
                String serverIp = args[2];
                Client client = new Client(clientUsername, serverIp, 5424);
                client.run_session();
                break;

            case "-s": // Server mode
                if (args.length < 2) {
                    System.out.println("Server mode requires: <username>");
                    return;
                }
                String serverUsername = args[1];
                Server server = new Server(serverUsername, 5424);
                server.run_session();
                break;

            default:
                System.out.println("Invalid flag. Use -c for client or -s for server.");
                break;
        }
    }
}
