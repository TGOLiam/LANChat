package core;
import java.net.*;
public class Server extends Chat{
    public Server(String username, int port) throws Exception
    {
        System.out.println("Server opened...");
        // set username
        local_name = username;
        init(null, port);
    }

    @Override
    protected void init(String addr, int port) throws Exception
    {
        // setup server socket, and listen for connections
            broadcast();

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
            display_msg_history();
    }
}
