import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 4000;

    private static final String SERVER_ADDRESS = "localhost";

    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {

        System.out.println("====================================================");
        System.out.println("------CHAT SERVER IS RUNNING------");
        System.out.println("SERVER PORT :" + PORT);
        System.out.println("SERVER ADDRESS :" + SERVER_ADDRESS);
        System.out.println("====================================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("NEW CLIENT CONNECTED TO THE SERVER "+ clientSocket);
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast the message to all clients view
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // ---------Remove client handler from clientHandlers --------
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private String userName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            this.out = writer;

            writer.println("Enter your name here: ");
            userName = reader.readLine();
            System.out.println("===========New Client===========");
            System.out.println(userName + " has joined the chat.");
            System.out.println("================================");
            ChatServer.broadcast(userName + " has joined to your chat!", this);

            // Read and broadcast messages
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(userName + ": " + message);
                ChatServer.broadcast(userName + ": " + message, this);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
            ChatServer.broadcast(userName + " has left the chat.", this);
            System.out.println(userName + " disconnected from server.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
