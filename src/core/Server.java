
package core;
import java.net.*;
public class Server extends Chat{
    public Server(String username, int port) throws Exception
    {
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
    @Override
    public void start() throws Exception{
        System.out.println("Waiting for response...");
        while (true) {
            // Receive input to server, format, then save
            String buffer = receive();                  // read input from socket
            push_message(new Message(get_timestamp(), peer_name, buffer));          
            clear_terminal();
            display_msg_history();

            String input = get_input();
            send(input);
            push_message(new Message(get_timestamp(), local_name, input));
            clear_terminal();
            display_msg_history();

            System.out.println("Waiting for reply...");
        }
    }
}