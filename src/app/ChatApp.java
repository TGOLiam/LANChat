package app;

import core.Server;
import core.Client;
import core.Chat;
import java.util.Scanner;
import java.util.*;

public class ChatApp {
    public static void main(String[] args) {
        final int PORT = 5424;
        String username = "Guest";
        Scanner sc = new Scanner(System.in);

        Chat chat = null;
        Map<String, String> hosts = C;

        while (true) {
            System.out.println("\n=== LAN Chat App ===");
            System.out.println("Welcome, " + username + "!");
            System.out.println("------------------------------");
            System.out.println("Online Hosts:");
            if (hosts.isEmpty()) {
                System.out.println("  No hosts available at the moment.");
            }
            int i = 1;
            for (String name : hosts.keySet()) {
                System.out.println("  [" + i + "] " + name);
                i++;
            }
            System.out.println("------------------------------");
            System.out.println("[1] Host a chat");
            System.out.println("[2] Connect to host");
            System.out.println("[3] Change username");
            System.out.println("[0] Exit");
            System.out.print("Choose mode: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    try {
                        chat = new Server(username, PORT);
                        chat.start();
                    } catch (Exception e) {
                        System.err.println("Session terminated: " + e.getMessage());
                    }
                    finally{
                        if (chat != null) chat.terminate();
                    }
                    break;

                case 2:
                    System.out.print("Enter peer's username: ");
                    String peer_name = sc.nextLine();

                    String ip = hosts.get(peer_name);
                    if (ip == null){
                        System.out.println("Peer not found");
                        break;
                    }
                    try{
                        chat = new Client(username, ip, PORT);
                        chat.start();
                    }
                    catch (Exception e){
                        System.err.println("Session terminated: " + e.getMessage());
                    }
                    finally{
                        if (chat != null) chat.terminate();
                    }
                    break;
                case 3:
                    System.out.print("Enter new username: ");
                    username = sc.nextLine();
                    System.out.println("Username changed to: " + username);
                    break;
                case 4:
                    hosts = Chat.get_peers(PORT);
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
