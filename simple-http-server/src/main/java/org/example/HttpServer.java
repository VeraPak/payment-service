package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started at http://localhost:8080");

            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {
                while (!br.ready()) ;
                String fileName = br.readLine().split(" ")[1];
                URL resource = HttpServer.class.getClassLoader().getResource("static" + fileName);

                if (resource == null) {
                    out.write("HTTP/1.1 404 Not Found\r\n");
                    out.write("Content-Type: text/html; charset=UTF-8\r\n");
                    out.write("\r\n");
                    out.flush();

                    return;
                }

                Path filePath = Paths.get(resource.toURI());
                byte[] content = Files.readAllBytes(filePath);
                String contentType = Files.probeContentType(filePath);
                String response = new String(content, StandardCharsets.UTF_8);

                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: " + contentType + "\r\n");
                out.write("Content-Length: " + content.length + "\r\n");
                out.write("\r\n");
                out.write(response);
                out.flush();


            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}