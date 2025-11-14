
package core;
import java.io.IOException;
import java.net.*;
public class Client extends Chat{
    public Client(String username, String addr, int port) throws Exception
    {
        System.out.println("Connecting...");
        local_name = username;           // Set username
        init(addr, port);
    }

    @Override
    protected void init(String addr, int port) throws Exception {
        // Try many times if server is closed
        int i = 0;
        while (true) {
            try {
                socket = new Socket(addr, port);
                break; // connected successfully
            } catch (IOException e) {
                if (i++ > 5) throw new Exception("Cant connect to server.");
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
        display_msg_history();
    }
}
