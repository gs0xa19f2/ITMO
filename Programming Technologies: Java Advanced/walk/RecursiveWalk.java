package info.kgeorgiy.ja.gusev.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk {
    private static int calculateJenkinsHash(Path path) {
        int hash = 0;
        try (InputStream inputStream = Files.newInputStream(path)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    hash += (buffer[i] & 0xFF);
                    hash += (hash << 10);
                    hash ^= (hash >>> 6);
                }
            }
            hash += (hash << 3);
            hash ^= (hash >>> 11);
            hash += (hash << 15);
        } catch (IOException e) {
            hash = 0;
        }
        return hash;
    }

    private static void walk(String input, String output) {
        Path inputPath;
        Path outputPath;

        try {
            inputPath = Paths.get(input);
            outputPath = Paths.get(output);
        } catch (InvalidPathException e) {
            System.err.println("ERROR: InvalidPathException: " + e.getMessage());
            return;
        }

        if (outputPath.getParent() != null) {
            try {
                Files.createDirectories(outputPath.getParent());
            } catch (IOException e) {
                System.err.println("ERROR: IOException can't create folder for output file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                String filePathString;

                while ((filePathString = reader.readLine()) != null) {
                    try {
                        Path filePath = Paths.get(filePathString);

                        Files.walkFileTree(filePath, new SimpleFileVisitor<>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                writer.write(String.format("%08x %s", calculateJenkinsHash(file), file.toString()));
                                writer.newLine();
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                writer.write(String.format("%08x %s", 0, file.toString()));
                                writer.newLine();
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (InvalidPathException e) {
                        writer.write(String.format("%08x %s", 0, filePathString));
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                System.err.println("ERROR: IOException while writing to output file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("ERROR: IOException while reading from input file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Usage: java RecursiveWalk <input file> <output file>");
        } else {
            walk(args[0], args[1]);
        }
    }
}

