package app;

import core.Server;
import core.Client;
import core.Chat;

import java.util.*;

public class ChatApp {
    private static final int PORT = 5424;

    public static void main(String[] args) {
        String username = "Guest";
        Scanner sc = new Scanner(System.in);

        Chat chat = null;

        while (true) {
            System.out.println("\n=== LAN Chat App ===");
            System.out.println("Welcome, " + username + "!");
            System.out.println("------------------------------");
            System.out.println("[1] Start hosting");
            System.out.println("[2] Connect via IP Address");
            System.out.println("[3] Change username");
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
            if(choice == 1){
                try{
                    chat = new Server(username, PORT);
                    chat.start();
                } catch (Exception e) {
                    System.err.println("Session cant start: " + e.getMessage());
                }
                finally{
                    chat.terminate();
                }
            }
            else if (choice == 2){
                System.out.print("Enter computer's address: ");
                ip = sc.nextLine();
                try{
                    chat = new Client(username, ip, PORT);
                    chat.start();
                } catch (Exception e) {
                    System.err.println("Session cant start: " + e.getMessage());
                }
                finally{
                    chat.terminate();
                }
            }
            else if (choice == 3){
                System.out.print("Enter new username: ");
                username = sc.nextLine();
                System.out.println("Username changed to: " + username);
            }
            else if (choice == 0){
                System.out.println("Exiting Chat App...");
                sc.close();
                return;
            }
            else System.out.println("Invalid choice. Try again.");
        }
    }
}
