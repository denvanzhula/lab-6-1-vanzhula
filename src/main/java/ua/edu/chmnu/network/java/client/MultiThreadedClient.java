package ua.edu.chmnu.network.java.client;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedClient {
    private static final int THREAD_COUNT = 1000;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new ClientSession(i + 1));
        }

        threadPool.shutdown();
    }

    static class ClientSession implements Runnable {
        private int sessionId;

        public ClientSession(int sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String randomMessage = generateRandomString(10);

                writer.println(randomMessage);

                String response = reader.readLine();

                System.out.println("Session " + sessionId + ": Sent '" + randomMessage + "', Received '" + response + "'");

            } catch (IOException e) {
                System.err.println("Session " + sessionId + " error: " + e.getMessage());
            }
        }

        private String generateRandomString(int length) {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            return sb.toString();
        }
    }
}
