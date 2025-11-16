package core;
import java.net.*;
public class Server extends Chat{
    public Server(String username, int port) throws Exception
    {
        // set username
        local_name = username;
        init(null, port);
    }

    @Override
    protected void init(String addr, int port) throws Exception
    {
            System.out.println("Server opened...");
        // setup server socket, and listen for connections
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(port));
            server.setSoTimeout(SOCKET_TIMEOUT_MS); // 60 seconds read timeout

            socket = server.accept();
            socket.setSoTimeout(SOCKET_TIMEOUT_MS); // 60 seconds read timeout

            System.out.println("Incoming request from " + socket.getInetAddress());
            System.out.print("Accept? (y/n): ");
            String input = sc.nextLine();

            switch (input)
            {
                case "y": case "Y":
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
                    break;
                case "n":case "N": default:
                    socket.close();
                    server.close();
                    init(addr, port);
            }
    }
}
