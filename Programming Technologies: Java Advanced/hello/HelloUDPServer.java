package info.kgeorgiy.ja.gusev.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class HelloUDPServer implements NewHelloServer {
    private ExecutorService executor;
    private volatile boolean running = true;
    private final List<DatagramSocket> serverSockets = Collections.synchronizedList(new ArrayList<>());
    private final List<Thread> receiverThreads = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void start(int threads, Map<Integer, String> ports) {
        executor = Executors.newFixedThreadPool(threads);
        for (Map.Entry<Integer, String> entry : ports.entrySet()) {
            int port = entry.getKey();
            String format = entry.getValue();
            try {
                DatagramSocket socket = new DatagramSocket(port);
                serverSockets.add(socket);
                Thread receiver = new Thread(new Receiver(socket, format));
                receiver.start();
                receiverThreads.add(receiver);
            } catch (SocketException e) {
                System.err.println("Error creating socket on port " + port + ": " + e.getMessage());
                stop();
            }
        }
    }

    private class Receiver implements Runnable {
        private final DatagramSocket socket;
        private final String format;

        Receiver(DatagramSocket socket, String format) {
            this.socket = socket;
            this.format = format;
        }

        @Override
        public void run() {
            while (running && !socket.isClosed()) {
                byte[] buffer;
                try {
                    buffer = new byte[socket.getReceiveBufferSize()];
                } catch (SocketException e) {
                    System.err.println("SocketException occurred: " + e.getMessage());
                    buffer = new byte[1024]; 
                }
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(requestPacket);
                    
                    String request = new String(requestPacket.getData(), requestPacket.getOffset(), requestPacket.getLength(), StandardCharsets.UTF_8);
                    SocketAddress clientAddress = requestPacket.getSocketAddress();
                    executor.submit(() -> handleRequest(request, format, socket, clientAddress));
                } catch (SocketException e) {
                    
                    break;
                } catch (IOException e) {
                    System.err.println("Error receiving packet on port " + socket.getLocalPort() + ": " + e.getMessage());
                }
            }
        }
    }

    private void handleRequest(String request, String format, DatagramSocket socket, SocketAddress clientAddress) {
        String response = format.replace("$", request);
        byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress);
        try {
            synchronized (socket) {
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            System.err.println("Error sending response to " + clientAddress + ": " + e.getMessage());
        }
    }

    @Override
    public void close() {
        stop();
    }

    private void stop() {
        running = false;
        
        synchronized (serverSockets) {
            for (DatagramSocket s : serverSockets) {
                if (!s.isClosed()) {
                    s.close();
                }
            }
            serverSockets.clear();
        }
        
        synchronized (receiverThreads) {
            for (Thread t : receiverThreads) {
                t.interrupt();
            }
            receiverThreads.clear();
        }
        
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Использование: HelloUDPServer <port> <threads>");
            System.out.println("Или для мультипорта: HelloUDPServer <threads> <port1> <format1> <port2> <format2> ...");
            return;
        }

        HelloUDPServer server = new HelloUDPServer();
        try {
            if (args.length == 2) {
                int port = Integer.parseInt(args[0]);
                int threads = Integer.parseInt(args[1]);
                server.start(threads, Map.of(port, "Hello, $"));
            } else {
                int threads = Integer.parseInt(args[0]);
                if ((args.length - 1) % 2 != 0) {
                    System.err.println("Неправильное количество аргументов для мультипорта.");
                    return;
                }
                Map<Integer, String> ports = new ConcurrentHashMap<>();
                for (int i = 1; i < args.length; i += 2) {
                    int port = Integer.parseInt(args[i]);
                    String format = args[i + 1];
                    ports.put(port, format);
                }
                server.start(threads, ports);
            }
        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга чисел: " + e.getMessage());
            server.close();
        }
    }
}
