
package core;
import java.net.*;
public class Server extends Chat{
    private ServerSocket server = null;
    public Server(String username, int port)
    {
        try{
            System.out.println("Server opened...");

            // set username
            local_name = username;

            // setup server socket, and listen for connections
            server = new ServerSocket(port);
            socket = server.accept();
            socket.setSoTimeout(SOCKET_TIMEOUT_MS); // 60 seconds read timeout

            // Set up streams first
            initialize_streams();

            // Exchange usernames
            send(local_name);
            peer_name = receive();

            // initialize logs
            initialize_logs();

            // Notify connection
            clear_terminal();
            System.out.println("Connected to " + peer_name + " at "+ socket.getInetAddress());
        }
        catch (Exception e){  
            System.err.println("Session terminated: " + e.getMessage());
        }
    }
    @Override
    public void run_session(){
        try{
            System.out.println("Waiting for response...");
            while (true) {
                // Receive input to server, format, then save
                String buffer = receive();                  // read input from socket
                Message rec_msg = new Message(get_timestamp(), peer_name, buffer); // format buffer as Message object
                push_message(rec_msg);          

                clear_terminal();
                display_msg_history();

                String input = get_input();
                send(input);
                Message sent_msg = new Message(get_timestamp(), local_name, input);
                push_message(sent_msg);
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
            server.close();;
            sc.close();
            writer.close();
            reader.close();
        }
        catch (Exception e){
            System.err.println("Session cant exit properly: " + e.getMessage());
        }
    }
}