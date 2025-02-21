package edu.eci.arsw.parcial.http;

import java.net.*;
import java.io.*;

public class ChatGPT {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String request = "";
            while ((inputLine = in.readLine()) != null) {
                request += inputLine + "\r\n";
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            HttpRequest httpRequest = new HttpRequest(request);
            HttpResponse httpResponse = manageRequest(httpRequest);
            
            out.println(httpResponse.toString());
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    
    public static HttpResponse manageRequest(HttpRequest httpRequest){
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(200);
        httpResponse.setContentType("application/json");
        String body = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Mi propio mensaje</h1>\n"
                + "</body>\n"
                + "</html>\n";
        httpResponse.setBody(body);
        return httpResponse;
    }
}
