
package core;
import java.io.IOException;
import java.net.*;
public class Client extends Chat{
    public Client(String username, String addr, int port)
    {
        try{
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
            // Set up streams first
            initialize_streams();

            // Initialize usernames
            peer_name = receive(); // Find server's name
            send(local_name);       // Send client's name

            // Initialize the session
            initialize_logs();

            clear_output();
            System.out.println("Connected to " + peer_name + " at "+ socket.getInetAddress());
        }
        catch (Exception e){  
            System.err.println("Session terminated: " + e.getMessage());
        }
    }

    @Override
    public void run_session(){
        try{
            while (true) {
                // Sending input to server, format, then save
                String input = get_input();     // read input from stdin
                send(input);                        // read input from socket
                Message sent_msg = new Message(get_timestamp(), local_name, input); // format input as Message object
                push_message(sent_msg);         // save to msg history
                            
                // Receive input to server, format, then save
                String buffer = receive();                  // read input from socket
                Message rec_msg = new Message(get_timestamp(), peer_name, buffer); // format buffer as Message object
                push_message(rec_msg);              // Save to msg history

                clear_output();
                display_msg_history();
            }
        }
        catch (Exception e) {
            System.err.println("Session terminated: " + e.getMessage());
            exit_session();
        }
    }

    @Override
    public void exit_session()
    {
        try{
            // Log messages
            for (Message message : msg_history) {
                log_message(message);
            }

            socket.close();
            sc.close();
            writer.close();
            reader.close();
        }
        catch (Exception e){
            System.err.println("Session cant exit properly: " + e.getMessage());
        }
    }
}