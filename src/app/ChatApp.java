package app;

import core.Server;
import core.Client;
import core.Chat;

import java.util.*;

public class ChatApp {
    private static Map<String, String> hosts = new HashMap<>();
    private static final int PORT = 5424;

    public static void main(String[] args) {
        String username = "Guest";
        Scanner sc = new Scanner(System.in);

        Chat chat = null;

        while (true) {
            System.out.println("\n=== LAN Chat App ===");
            System.out.println("Welcome, " + username + "!");
            System.out.println("------------------------------");

            // Display online hosts dynamically
            System.out.println("Online sessions:");
            if (hosts.isEmpty()) {
                System.out.println("  No sessions available at the moment.");
            } else {
                int index = 1;
                for (String host : hosts.keySet()) {
                    System.out.println("  [" + index + "] " + host);
                    index++;
                }
            }
            System.out.println("------------------------------");
            System.out.println("[1] Start hosting");
            System.out.println("[2] Join a session");
            System.out.println("[3] Connect via IP Address");
            System.out.println("[4] Change username");
            System.out.println("[5] Refresh session list");
            System.out.println("[0] Exit");
            System.out.print("Choose mode: ");

            int choice;
            try{
                choice = sc.nextInt();
                sc.nextLine(); // consume newline
            }
            catch (InputMismatchException ime)
            {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            String ip = null;
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
                    if (hosts.isEmpty()) {
                        System.out.println("No peers found");
                        update_hosts();
                        break;
                    }
                    System.out.print("Enter peer's username: ");
                    String peer_name = sc.nextLine();

                    ip = hosts.get(peer_name);
                    if (ip == null){
                        System.out.println("Peer not found");
                        break;
                    }

                    try{
                        run(new Client(username, ip, PORT));
                    }
                    catch (Exception e){
                        System.err.println("Session cant start: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter computer's address: ");
                    ip = sc.nextLine();

                    try{
                        run(new Client(username, ip, PORT));
                    }
                    catch (Exception e){
                        System.err.println("Session cant start: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Enter new username: ");
                    username = sc.nextLine();
                    System.out.println("Username changed to: " + username);
                    break;
                case 5:
                    update_hosts();
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
    private static void run(Chat chat){
        try{ chat.start(); }
        catch (Exception e ) { System.err.println("Session terminated: " + e.getMessage()); }
        finally { if (chat != null) chat.terminate(); }
    }

    private static void update_hosts()
    {
        hosts.clear();
        System.out.println("Finding hosts...");
        hosts = Chat.get_peers();
        if (hosts.isEmpty()) System.out.println("No peers found, try connecting thru address instead.");
        else System.out.println("Peers found!");
    }
}
