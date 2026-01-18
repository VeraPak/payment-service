package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    public static void main(String[] args) {
        Path templatesDir = validateArgs(args);

        int port = 8080;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started at http://localhost:" + port);

            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("New client connected");
                handleClient(clientSocket, templatesDir);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Path validateArgs(String[] args) {
        if (args.length == 0) {
            System.exit(1);
        }

        Path dir = Paths.get(args[0]).toAbsolutePath().normalize();
        if (!Files.isDirectory(dir)) {
            System.exit(1);
        }
        return dir;
    }

    private static void handleClient(Socket clientSocket, Path templatesDir) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream outputStream = clientSocket.getOutputStream()
        ) {
            while (!br.ready()) ;

            String fileName = br.readLine().split(" ")[1].substring(1);
            Path filePath = templatesDir.resolve(fileName).normalize();
            if (!filePath.startsWith(templatesDir) ||
                    !Files.exists(filePath) ||
                    !Files.isRegularFile(filePath)) {
                send404(outputStream);
                return;
            }
            sendFile(outputStream, filePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void send404(OutputStream outputStream) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            bufferedWriter.write("HTTP/1.1 404 Not Found\r\n");
            bufferedWriter.write("Content-Type: text/html; charset=UTF-8\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();
        }
    }

    private static void sendFile(OutputStream out, Path filePath) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
             InputStream is = Files.newInputStream(filePath)) {

            String contentType = Files.probeContentType(filePath);
            long contentLength = Files.size(filePath);

            bufferedWriter.write("HTTP/1.1 200 OK\r\n");
            bufferedWriter.write("Content-Type: " + contentType + "\r\n");
            bufferedWriter.write("Content-Length: " + contentLength + "\r\n");
            bufferedWriter.write("\r\n");
            bufferedWriter.flush();

            is.transferTo(out);
        }
    }

}