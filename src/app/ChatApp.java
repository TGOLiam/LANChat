package app;

import core.Server;
import core.Client;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) {
        final int PORT = 5424;
        String username = "Guest";
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Chat App ===");
            System.out.println("Welcome, " + username + "!");
            System.out.println("[1] Start as Server");
            System.out.println("[2] Start as Client");
            System.out.println("[3] Change Username");
            System.out.println("[0] Exit");
            System.out.print("Choose mode: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    Server server = null;
                    try {
                        server = new Server(username, PORT);
                        server.start();
                    } catch (Exception e) {
                        System.err.println("Session terminated: " + e.getMessage());
                    }
                    finally{
                        if (server != null) server.terminate();
                    }

                    break;

                case 2:
                    System.out.print("Enter IP address: ");
                    String ip = sc.nextLine();

                    Client client = null;
                    try{
                        client = new Client(username, ip, PORT);
                        client.start();
                    }
                    catch (Exception e){
                        System.err.println("Session terminated: " + e.getMessage());
                    }
                    finally{
                        if (client != null) client.terminate();
                    }

                    break;
                case 3:
                    System.out.print("Enter new username: ");
                    username = sc.nextLine();
                    System.out.println("Username changed to: " + username);
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
