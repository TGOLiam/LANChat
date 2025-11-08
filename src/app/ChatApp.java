package app;
import core.Server;
import core.Client;
import java.util.Scanner;

public class ChatApp {
    public static void main(String[] args) {
        int port = 5424;
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Chat App Setup ===");
        System.out.println("[1] Start as Server");
        System.out.println("[2] Start as Client");
        System.out.print("Choose mode: ");
        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Enter username: ");
            String username = sc.nextLine();

            Server server = new Server(username, port);
            server.run_session();
        } 
        else if (choice == 2) {
            System.out.print("Enter username: ");
            String username = sc.nextLine();

            System.out.print("Enter IP address: ");
            String ip = sc.nextLine();

            Client client = new Client(username, ip, port);
            client.run_session();
        } 
        else {
            System.out.println("Invalid choice.");
        }
        sc.close();
    }
}
