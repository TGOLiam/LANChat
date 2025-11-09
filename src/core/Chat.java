package core;
import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public abstract class Chat{
    // Socket classes for networking
    protected Socket socket = null;            
    protected ServerSocket server = null;
    
    protected Deque<Message> msg_history = new ArrayDeque<>();      // For easy deletion/addition

    // Input/Output Streams
    protected DataInputStream socket_in = null;            
    protected DataOutputStream socket_out = null;          
    protected BufferedWriter writer = null;
    protected BufferedReader reader = null;
    protected Scanner sc = new Scanner(System.in);

    // Usernames
    protected String local_name = null;
    protected String peer_name = null;

    // Constants
    protected final byte MAX_RECENT_MSG = 10;
    protected final int SOCKET_TIMEOUT_MS = 60_000;

    // methods to be implemented by subclasses
    public abstract void start() throws Exception;

    // initialization methods
    protected void initialize_logs() throws Exception 
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
    protected void initialize_streams() throws Exception
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
    protected void send(String buffer) throws Exception
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
    protected String receive() throws Exception
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
    protected void log_message(Message m) throws Exception         // Save message into file
    {
        try {
            writer.write(String.format("[%s] %s: %s\n", m.timestamp, m.user, m.message));
            writer.flush(); 
        } catch (IOException e) {
            throw new Exception("Cant write to file.");
        }
    }
    protected void push_message(Message m) throws Exception             // Load message into msg history
    {
        msg_history.addLast(m);                     // add to deque
        if (msg_history.size() > MAX_RECENT_MSG)        // if size > 10
            log_message(msg_history.removeFirst());                          // save to file 
        
    }
    protected String get_input() throws Exception
    {
        System.out.flush();
        System.out.print("Enter message >> ");
        String input = sc.nextLine();
        if (input.equals("/exit")) 
            throw new Exception("User Exited"); 
        return input;
    }
    // Utility methods
    protected void display_msg_history()
    {
        System.out.println("Connected to " + peer_name + " at "+ socket.getInetAddress());
        for (Message m : msg_history) 
            System.out.printf("[%s] %s\n", m.user, m.message);
    }
    protected String get_timestamp() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(f);
    }
    protected void clear_terminal() {
        System.out.print("\033[K\033c");
        System.out.flush();
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
}