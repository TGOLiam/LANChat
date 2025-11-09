package core;
import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class

public abstract class Chat{
    protected Socket socket = null;            // Socket for networking
    protected Deque<Message> msg_history = null;      // For easy deletion/addition

    // Input/Output Streams
    protected DataInputStream socket_in = null;            
    protected DataOutputStream socket_out = null;          
    protected BufferedWriter writer = null;
    protected BufferedReader reader = null;
    protected Scanner sc = null;

    // Usernames
    protected String local_name = null;
    protected String peer_name = null;

    // Constants
    protected final byte MAX_RECENT_MSG = 10;
    protected final int SOCKET_TIMEOUT_MS = 60_000;

    // methods to be implemented by subclasses
    public abstract void run_session();
    public abstract void exit_session();

    // Initialize deque and scanner
    Chat()
    {
        msg_history = new ArrayDeque<>();
        sc = new Scanner(System.in);
    }

    // initialization methods
    protected void initialize_logs() throws Exception 
    {
        try {
            File log_folder = new File("logs");
            if (!log_folder.exists()) {
                log_folder.mkdirs(); // create the directory if missing
            }

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
        catch (SocketException e) {
            throw new Exception("Connection disconnected. ");
        } 
        catch (EOFException e) {
            throw new Exception("Connection closed. ");
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
        catch (SocketException e) {
            throw new Exception("Connection disconnected. ");
        } 
        catch (EOFException e) {
            throw new Exception("Connection closed. ");
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
            String formatted = String.format("[%s] %s: %s\n", m.timestamp, m.user, m.message);
            writer.write(formatted);
            writer.flush(); 
        } catch (IOException e) {
            throw new Exception("Cant write to file.");
        }
    }
    protected void push_message(Message m) throws Exception             // Load message into msg history
    {
        try{
            msg_history.addLast(m);                     // add to deque
            if (msg_history.size() > MAX_RECENT_MSG)        // if size > 10
            {
                Message last_msg = msg_history.removeFirst();   // get last message
                log_message(last_msg);                          // save to file 
            }
        }
        catch (Exception e)
        {
            throw new Exception("Cant save to history.");
        }
    }
    protected String get_input() throws Exception
    {
        System.out.flush();
        System.out.print("Enter message >> ");
        String input;
        while (true)
        {
            input = sc.nextLine();
            if (input.startsWith("/"))
            {
                switch (input) {
                    case "/exit":
                        throw new Exception("Exited");
                    default:
                        System.out.println("Unknown command: " + input);
                        continue;
                }
            }
            System.out.println("Waiting for reply...");
            return input;
        }
    }
    // Utility methods
    protected void display_msg_history()
    {
        for (Message m : msg_history) {
            System.out.printf("[%s] %s\n", m.user, m.message);
        }
    }
    protected String get_timestamp() {
        LocalDateTime t = LocalDateTime.now();
        // Format: YYYY-MM-DD HH:MM:SS
        DateTimeFormatter formatted_t = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return t.format(formatted_t);
    }
    protected void clear_terminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}