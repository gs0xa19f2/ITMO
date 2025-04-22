package info.kgeorgiy.ja.gusev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class HelloUDPClient implements HelloClient {
    private static final int TIMEOUT = 1000; 

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
            return;
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            final int threadNumber = i;
            threadPool.submit(() -> {
                Thread.currentThread().setName("Client-Thread-" + threadNumber);
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(TIMEOUT);
                    for (int j = 0; j < requests; j++) {
                        String request = prefix + (threadNumber + 1) + "_" + (j + 1);
                        byte[] requestData = request.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, port);
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                socket.send(requestPacket);
                                byte[] buffer = new byte[socket.getReceiveBufferSize()];
                                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                                socket.receive(responsePacket);
                                String response = new String(responsePacket.getData(), responsePacket.getOffset(), responsePacket.getLength(), StandardCharsets.UTF_8);
                                if (isValidResponse(response, threadNumber + 1, j + 1)) {
                                    System.out.println("Received: " + response);
                                    break;
                                }
                            } catch (SocketTimeoutException e) {
                                
                            } catch (IOException e) {
                                
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("Socket error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            threadPool.shutdownNow();
        }
    }

    private boolean isValidResponse(String response, int threadNumber, int requestNumber) {
        
        Pattern numberPattern = Pattern.compile("\\p{Nd}+");
        Matcher matcher = numberPattern.matcher(response);
        boolean threadFound = false;
        boolean requestFound = false;
        while (matcher.find()) {
            String numberStr = matcher.group();
            int number = parseUnicodeNumber(numberStr);
            if (number == threadNumber && !threadFound) {
                threadFound = true;
            } else if (number == requestNumber && !requestFound) {
                requestFound = true;
            }
            if (threadFound && requestFound) {
                return true;
            }
        }
        return false;
    }

    private int parseUnicodeNumber(String numberStr) {
        int number = 0;
        for (int i = 0; i < numberStr.length(); i++) {
            char c = numberStr.charAt(i);
            int digit = Character.getNumericValue(c);
            if (digit < 0 || digit > 9) {
                return -1; 
            }
            number = number * 10 + digit;
        }
        return number;
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: HelloUDPClient <host> <port> <prefix> <threads> <requests>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String prefix = args[2];
        int threads = Integer.parseInt(args[3]);
        int requests = Integer.parseInt(args[4]);

        HelloUDPClient client = new HelloUDPClient();
        client.run(host, port, prefix, threads, requests);
    }
}

