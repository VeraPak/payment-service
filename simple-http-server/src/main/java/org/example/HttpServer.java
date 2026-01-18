package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {

    public static void main(String[] args) throws IOException {
        Path templatesDir;
        if (args.length == 0) {
            System.exit(1);
        }
        templatesDir = Paths.get(args[0]);
        if(!Files.isDirectory(templatesDir)){
            System.exit(1);
        }

        int port = 8080;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started at http://localhost:" + port);

            try (Socket socket = serverSocket.accept();
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
            ) {
                System.out.println("New client connected");

                while (!br.ready());

                String fileName = br.readLine().split(" ")[1].substring(1);
                Path filePath = templatesDir.resolve(fileName).normalize();
                if (!filePath.startsWith(templatesDir) ||
                        !Files.exists(filePath) ||
                        !Files.isRegularFile(filePath)) {
                    out.write("HTTP/1.1 404 Not Found\r\n");
                    out.write("Content-Type: text/html; charset=UTF-8\r\n");
                    out.write("\r\n");
                    out.flush();

                    return;
                }

                String contentType = Files.probeContentType(filePath);
                long contentLength = Files.size(filePath);

                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: " + contentType + "\r\n");
                out.write("Content-Length: " + contentLength + "\r\n");
                out.write("\r\n");
                out.flush();

                OutputStream os = socket.getOutputStream();
                try (InputStream is = Files.newInputStream(filePath)) {
                    is.transferTo(os);
                }
                os.flush();
            }
        }
    }
}