package app;

import core.Server;
import core.Client;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) {
        final int PORT = 5424;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Chat App Setup ===");
            System.out.println("[1] Start as Server");
            System.out.println("[2] Start as Client");
            System.out.println("[0] Exit");
            System.out.print("Choose mode: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String serverUser = sc.nextLine();
                    Server server = new Server(serverUser, PORT);
                    server.start();
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String clientUser = sc.nextLine();

                    System.out.print("Enter IP address: ");
                    String ip = sc.nextLine();

                    Client client = new Client(clientUser, ip, PORT);
                    client.start();
                    break;

                case 0:
                    System.out.println("Exiting Chat App...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }
}
