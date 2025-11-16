package core;
import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public abstract class Chat{
    // Socket classes for networking
    Socket socket = null;
    ServerSocket server = null;
    Exception receiverException = null;                    // To handle exceptions in the receiver thread
    Deque<Message> msg_history = new ArrayDeque<>();      // For easy deletion/addition

    // Input/Output Streams
    DataInputStream socket_in = null;
    DataOutputStream socket_out = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    Scanner sc = new Scanner(System.in);

    // Usernames
    String local_name = null;
    String peer_name = null;

    // Constants
    final byte MAX_RECENT_MSG = 10;
    final int SOCKET_TIMEOUT_MS = 60_000;
    static final int DISCOVERY_PORT = 5425;

    // methods to be implemented by subclasses
    protected abstract void init(String addr, int port) throws Exception;

    // methods
    public void start() throws Exception {
        // Single long-lived thread for receiving messages
        new Thread(() -> {
            try{
                while (true) push_message(new Message(get_timestamp(), peer_name, receive()));
            }catch(Exception e){
                receiverException = e;
            }
        }).start();

        // Main loop: send messages and display history
        while (true) {
            if (receiverException != null) {
                throw new Exception(receiverException.getMessage());
            }

            String input = get_input(); // block until user types
            if (input != null)
            {
                send(input);
                push_message(new Message(get_timestamp(), local_name, input));
            }
            clear_terminal();
            display_msg_history();
        }
    }
    public void terminate() {
        try {
            // Log remaining messages from history
            for (Message message : msg_history)
                log_message(message);

            // Close all resources
            if (socket != null) socket.close();
            if (server != null) server.close();
            if (writer != null) writer.close();
            if (reader != null) reader.close();
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    // initialization methods
    void initialize_logs() throws Exception
    {
        try {
            new File("logs").mkdirs(); // Creates directory if it doesn't exist

            // Set up log files
            String filename = "logs/" + local_name + "_" + peer_name + ".txt";
            writer = new BufferedWriter(new FileWriter(filename, true)); // append mode
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException e) {
            throw new Exception("Cannot initialize logs.");
        }
    }
    void initialize_streams() throws Exception
    {
        try {
            // Data Stream is for reading/writing typed data such as readUTF() to read strings from socket
            // Buffered Stream is for improving efficiency
            // getInputStream() or getOutputStream() returns the stream of the socket
            socket_in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            socket_out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new Exception("Cannot initialize streams");
        }
    }

    // core methods
    void send(String buffer) throws Exception
    {
        try{
            socket_out.writeUTF(buffer);
            socket_out.flush();
        }
        catch (EOFException | SocketException e) {
            throw new Exception("Connection lost. ");
        }
        catch (SocketTimeoutException e)
        {
            throw new Exception("Socket timed out.");
        }
        catch (IOException e) {
            throw new Exception("Cant write to socket. ");
        }
    }
    String receive() throws Exception
    {
        try{
            return socket_in.readUTF();
        }
        catch (EOFException | SocketException e) {
            throw new Exception("Connection lost. ");
        }
        catch (SocketTimeoutException e)
        {
            throw new Exception("Socket timed out.");
        }
        catch (IOException e) {
            throw new Exception("Cant read from socket. ");
        }
    }
    void log_message(Message m) throws Exception         // Save message into file
    {
        try {
            writer.write(String.format("[%s %s] %s: %s\n", get_date(),m.timestamp, m.user, m.message));
            writer.flush();
        } catch (IOException e) {
            throw new Exception("Cant write to file.");
        }
    }
    void push_message(Message m) throws Exception             // Load message into msg history
    {
        msg_history.addLast(m);                     // add to deque
        if (msg_history.size() > MAX_RECENT_MSG)        // if size > 10
            log_message(msg_history.removeFirst());                          // save to file

    }
    String get_input() throws Exception
    {
        System.out.flush();
        System.out.print("Enter message >> ");
        String input = sc.nextLine();
        if (input.equals("/exit")) throw new Exception("User Exited");
        else if (input.isBlank()) return null;
        else if (input.equals("/help")){
            System.out.println("Press [Enter] to refresh messages (leave blank to skip sending)");
            System.out.println("/exit - Exit the chat");
            System.out.println("/help - Display this help message");

            System.out.println("\nPress [Enter] to continue...");
            sc.nextLine();
            return null;
        }

        return input;
    }
    // Utility methods
    void display_msg_history()
    {
        System.out.println("[Connected to " + peer_name + "]");
        System.out.println("Type /help for guide");
        for (Message m : msg_history)
            System.out.printf("[%s %s] %s\n", m.timestamp, m.user, m.message);
    }
    String get_timestamp() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
        return LocalDateTime.now().format(f);
    }
    String get_date() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.now().format(f);
    }
    String get_time() {
        return get_date() + " " + get_timestamp();
    }
    void clear_terminal() {
        System.out.print("\033[K\033c");
        System.out.flush();
    }
}
