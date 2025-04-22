package info.kgeorgiy.ja.gusev.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: HelloUDPNonblockingClient <host> <port> <prefix> <threads> <requests>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String prefix = args[2];
        int threads = Integer.parseInt(args[3]);
        int requests = Integer.parseInt(args[4]);

        new HelloUDPNonblockingClient().run(host, port, prefix, threads, requests);
    }
}

