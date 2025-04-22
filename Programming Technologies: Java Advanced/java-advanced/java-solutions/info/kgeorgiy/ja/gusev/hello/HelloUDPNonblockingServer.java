package info.kgeorgiy.ja.gusev.hello;

import info.kgeorgiy.java.advanced.hello.NewHelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


    public static void main(String[] args) {
        if (args.length < 2 || (args.length - 1) % 2 != 0) {
            System.out.println("Usage: HelloUDPNonblockingServer <port> <threads>");
            System.out.println("Or for multiple ports: HelloUDPNonblockingServer <threads> <port1> <format1> <port2> <format2> ...");
            return;
        }

        Map<Integer, String> ports = new HashMap<>();

        if (args.length == 2) {
            int port = Integer.parseInt(args[0]);
            String format = args[1];
            ports.put(port, format);
        } else {
            int threads = Integer.parseInt(args[0]); 
            for (int i = 1; i < args.length; i += 2) {
                int port = Integer.parseInt(args[i]);
                String format = args[i + 1];
                ports.put(port, format);
            }
        }

        HelloUDPNonblockingServer server = new HelloUDPNonblockingServer();
        server.start(1, ports);
    }
}

