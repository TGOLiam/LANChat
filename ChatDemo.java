import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatDemo {
    private static final List<String> messageHistory = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Thread simulating incoming messages
        Thread messageProducer = new Thread(() -> {
            int count = 1;
            while (true) {
                try {
                    Thread.sleep(1000); // simulate incoming message
                    synchronized (messageHistory) {
                        messageHistory.add("Incoming message #" + count++);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        messageProducer.start();

        // Main input loop
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (!input.isEmpty()) {
                synchronized (messageHistory) {
                    messageHistory.add("You: " + input);
                }
            }

            if (input.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            // Refresh console after input
            redrawMessages();
        }
    }

    private static void redrawMessages() {
        System.out.print("\033[H\033[2J"); // clear screen
        System.out.flush();

        synchronized (messageHistory) {
            for (String msg : messageHistory) {
                System.out.println(msg);
            }
        }
    }
}
