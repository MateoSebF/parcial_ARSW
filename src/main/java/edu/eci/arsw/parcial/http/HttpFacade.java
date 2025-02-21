package edu.eci.arsw.parcial.http;

import java.net.*;
import java.io.*;

public class HttpFacade {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(45000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 45000.");
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
            HttpResponse httpResponse = new HttpResponse();

            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // The following invocation perform the connection implicitly before getting the
            // code
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inputServer = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLineServer;
                StringBuffer response = new StringBuffer();

                while ((inputLineServer = inputServer.readLine()) != null) {
                    response.append(inputLineServer);
                }
                inputServer.close();

                // print result
                httpResponse.setStatusCode(responseCode);
                httpResponse.setContentType("application/json");
                System.out.println(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
            System.out.println("GET DONE");

            out.println(httpResponse.toString());
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static HttpResponse manageRequest(HttpRequest httpRequest) {
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
