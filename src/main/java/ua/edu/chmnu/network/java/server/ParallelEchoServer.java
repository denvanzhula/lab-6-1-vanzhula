package ua.edu.chmnu.network.java.server;

import java.io.*;
import java.net.*;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelEchoServer {
    private static final int PORT = 12345;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        System.out.println("Echo server started...");
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String receivedMessage;
                while ((receivedMessage = reader.readLine()) != null) {
                    Instant start = Instant.now();

                    String processedMessage = new StringBuilder(receivedMessage).reverse().toString();

                    Instant end = Instant.now();
                    Duration processingTime = Duration.between(start, end);

                    writer.println(processedMessage + " (Processed in " + processingTime.toMillis() + " ms)");

                    System.out.println("Processed message: " + receivedMessage + " -> " + processedMessage);
                }
            } catch (IOException e) {
                System.err.println("Client connection error: " + e.getMessage());
            }
        }
    }
}
