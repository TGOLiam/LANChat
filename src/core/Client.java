
package core;
import java.io.IOException;
import java.net.*;
public class Client extends Chat{
    public Client(String username, String addr, int port) throws Exception
    {
        System.out.println("Connecting...");
        local_name = username;           // Set username
        
        // Try many times if server is closed
        while (true) {
            try {
                socket = new Socket(addr, port);
                break; // connected successfully
            } catch (IOException e) {
                System.out.println("Server not available, retrying in 3 second...");
                Thread.sleep(3000);
            }
        }
        socket.setSoTimeout(SOCKET_TIMEOUT_MS); // 60 seconds read timeout
        
        // Set up streams first
        initialize_streams();

        // Initialize usernames
        peer_name = receive(); // Find server's name
        send(local_name);       // Send client's name

        // Initialize the session
        initialize_logs();

        clear_terminal();
        System.out.println("Connected to " + peer_name + " at "+ socket.getInetAddress());
        System.out.println("Type /exit for disconnection");
    }

    @Override
    public void start() throws Exception{
        while (true) {
            String input = get_input();    
            send(input);                        
            push_message(new Message(get_timestamp(), local_name, input));         // save to msg history
            clear_terminal();
            display_msg_history();

            System.out.println("Waiting for reply...");

            String buffer = receive();                  
            push_message(new Message(get_timestamp(), peer_name, buffer));              // Save to msg history
            clear_terminal();
            display_msg_history();
        }
    }
}