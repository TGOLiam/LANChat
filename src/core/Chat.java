package core;
import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public abstract class Chat{
    // Socket classes for networking
    Socket socket = null;                                   // Socket for connecting to the server
    ServerSocket server = null;                             // ServerSocket for listening for incoming connections
    Exception receiverException = null;                    // To handle exceptions in the receiver thread
    Deque<Message> msg_history = new ArrayDeque<>();      // For easy deletion/addition

    // Input/Output Streams
    DataInputStream socket_in = null;                       // DataInputStream for reading data from the socket
    DataOutputStream socket_out = null;                     // DataOutputStream for writing data to the socket
    BufferedWriter writer = null;                          // BufferedWriter for writing data to file
    BufferedReader reader = null;                          // BufferedReader for reading data from file
    Scanner sc = new Scanner(System.in);                   // Scanner for reading user input

    // Usernames
    String local_name = null;                              // Username of the local user
    String peer_name = null;                               // Username of the peer user

    // Constants
    final byte MAX_RECENT_MSG = 10;                         // Maximum number of recent messages to keep
    final int SOCKET_TIMEOUT_MS = 60_000;                   // Timeout for socket operations

    // methods to be implemented by subclasses
    protected abstract void init(String addr, int port) throws Exception;

    // methods
    public void start() throws Exception {
        // Single long-lived thread for receiving messages
        new Thread(() -> {
            try{
                // store received messages
                while (true) push_message(new Message(get_timestamp(), peer_name, receive()));
            } catch(Exception e){
                receiverException = e; // Log the exception
            }
        }).start();
        // Main loop: send messages and display history
        while (true) {
            if (receiverException != null) { // if receiver thread has an exception
                throw new Exception(receiverException.getMessage()); // Propagate the exception
            }

            String input = get_input(); // block until user types
            if (input != null)          // If user entered a message
            {
                send(input);            // Send the message
                push_message(new Message(get_timestamp(), local_name, input)); // Push the message to history
            }
            clear_terminal();           // Clear the terminal
            display_msg_history();      // Display the message history
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
            reader = new BufferedReader(new FileReader(filename)); // read mode
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
            socket_out.writeUTF(buffer);  // write the buffer to the socket
            socket_out.flush(); // flush the buffer to ensure data is sent
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
            return socket_in.readUTF(); // read the buffer from the socket
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
            // format message, write to file
            writer.write(String.format("[%s %s] %s: %s\n", get_date(), m.timestamp, m.user, m.message));
            writer.flush(); // flush the buffer to ensure data is written to file
        } catch (IOException e) {
            throw new Exception("Cant write to file.");
        }
    }
    void push_message(Message m) throws Exception             // Load message into msg history
    {
        msg_history.addLast(m);                     // add to history
        if (msg_history.size() > MAX_RECENT_MSG)        // if size > max recent messages
            log_message(msg_history.removeFirst());                          // get oldest message, then log it
    }
    String get_input() throws Exception
    {
        System.out.flush(); // flush the output buffer
        System.out.print("Enter message >> ");
        String input = sc.nextLine();
        if (input.equals("/exit")) throw new Exception("User Exited"); // exit the chat
        else if (input.equals("/help")){ // display help message
            System.out.println("Press [Enter] to refresh messages (leave blank to skip sending)");
            System.out.println("/exit - Exit the chat");
            System.out.println("/help - Display this help message");

            System.out.println("\nPress [Enter] to continue..."); // wait for user input
            sc.nextLine();
            return null; // return null to skip sending message
        }
        else if (input.isBlank()) return null; // return null to skip sending message

        return input; // return input to send message
    }
    // Utility methods
    void display_msg_history()
    {
        System.out.println("Connected to " + peer_name + " at "+ socket.getInetAddress()); // display connection info
        System.out.println("Type /help for guide"); // display help message
        for (Message m : msg_history)
            System.out.printf("[%s %s] %s\n", m.timestamp, m.user, m.message); // display message history
    }
    String get_timestamp() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a"); // format timestamp
        return LocalDateTime.now().format(f); // return formatted timestamp
    }
    String get_date() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // format date
        return LocalDateTime.now().format(f); // return formatted date
    }
    String get_time() { // Combines date and timestamp
        return get_date() + " " + get_timestamp();
    }

    void clear_terminal() { // Clears terminal screen
        System.out.print("\033[K\033c");
        System.out.flush();
    }

    void broadcast(int port) { // Broadcasts LANChat message to all devices on the network
        new Thread(() -> { // thread for broadcast
            try  {
                DatagramSocket socket = new DatagramSocket(); // Create a new DatagramSocket
                socket.setBroadcast(true); // Enable broadcast mode
                byte[] data = ("LANCHAT_BROADCAST_" + local_name).getBytes(); // Convert to bytes
                InetAddress addr = InetAddress.getByName("255.255.255.255"); // Convert to InetAddress
                DatagramPacket packet = new DatagramPacket(data, data.length, addr, port); // Create packet

                for (int i = 0; i < 10; i++) { // Broadcast packet every second
                    socket.send(packet); // Send packet
                    Thread.sleep(1000); // Wait for 1 second
                }
            } catch (Exception e) {
                System.err.println("Broadcast error: " + e.getMessage());
            }
        }).start();
    }

    public static Map<String, String> get_peers(int port) { // gets peers, returns map of peers (username, address)
        Map<String, String> peers = new HashMap<>(); // Initialize peers map
        try  {
            DatagramSocket socket = new DatagramSocket(port); // Create socket
            socket.setSoTimeout(1000); // Set timeout
            byte[] buf = new byte[256]; // Initialize buffer
            DatagramPacket packet = new DatagramPacket(buf, buf.length); // Initialize packet

            System.out.println("Finding hosts...\n");

            for (int i = 0; i < 10; i++) { // Receive packets
                try {
                    socket.receive(packet); // Receive packet
                    String msg = new String(packet.getData(), 0, packet.getLength()); // Convert into string
                    if (msg.startsWith("LANCHAT_BROADCAST_")) { // Check if message starts with LANCHAT_BROADCAST_
                        String name = msg.substring("LANCHAT_BROADCAST_".length()); // Extract name
                        peers.putIfAbsent(name, packet.getAddress().getHostAddress()); // Add peer to map
                    }
                } catch (SocketTimeoutException ignored) {} // Ignore timeout exception
            }
        } catch (Exception e) {
            System.err.println("Discovery error: " + e.getMessage());
        }
        return peers; // Return peers map
    }
}
