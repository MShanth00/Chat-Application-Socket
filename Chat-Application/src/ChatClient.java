import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 4000;

        public static void main(String[] args) {
            try (
                    Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true)
            ) {
                Scanner scanner = new Scanner(System.in);

                // Thread Part
                Thread readThread = new Thread(() -> {
                    try {
                        String serverMessage;
                        while ((serverMessage = reader.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("CONNECTION LOST : " + e.getMessage());
                    }
                });

                readThread.start();

                // Sending messages to the server view
                System.out.println("Connected to the chat server.");
                System.out.println("Enter your messages:");
                while (true) {
                    String userMessage = scanner.nextLine();
                    writer.println(userMessage);
                }
            } catch (IOException e) {
                System.err.println("Unable to connect to server: " + e.getMessage());
            }
        }
}

