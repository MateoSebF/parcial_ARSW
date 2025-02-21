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
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println(request);
            HttpResponse httpResponse = new HttpResponse();
            try {
                HttpRequest httpRequest = new HttpRequest(request);
                // http://localhost:45000/consulta?comando=binaryInvoke(java.lang.Math, max,
                // double, 4.5, double, -3.7)
                System.out.println(httpRequest.getUri().getPath());
                if (httpRequest.getUri().getPath().equals("/consulta")) {
                    URL obj;
                    if (httpRequest.getUri().getQuery() != null)
                        obj = new URL(GET_URL + "/compreflex?" + httpRequest.getUri().getQuery());
                    else
                        obj = new URL(GET_URL + "/compreflex");
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
                        httpResponse.setBody(response.toString());
                        System.out.println(response.toString());
                    } else {
                        httpResponse.setStatusCode(400);
                        httpResponse.setContentType("application/json");
                        httpResponse.setBody("");
                    }
                } else if (httpRequest.getUri().getPath().equals("/index.html")) {
                    httpResponse.setStatusCode(200);
                    httpResponse.setContentType("text/html");
                    httpResponse.setBody(getIndexHtml());
                } else {
                    System.out.println("GET request not worked");
                    httpResponse.setStatusCode(400);
                    httpResponse.setContentType("application/json");
                    httpResponse.setBody("");
                }
                System.out.println("GET DONE");
            } catch (Exception e) {
                System.out.println("GET request not worked" + e.getMessage());
                httpResponse.setStatusCode(400);
                httpResponse.setContentType("application/json");
                httpResponse.setBody("");
            }

            out.println(httpResponse.toString());
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static String getIndexHtml() {
        return "<!DOCTYPE html>\r\n" + //
                        "<html>\r\n" + //
                        "\r\n" + //
                        "<head>\r\n" + //
                        "    <title>Form Example</title>\r\n" + //
                        "    <meta charset=\"UTF-8\">\r\n" + //
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "</head>\r\n" + //
                        "\r\n" + //
                        "<body>\r\n" + //
                        "    <h1>Form to class</h1>\r\n" + //
                        "    <form action=\"/class\">\r\n" + //
                        "        <label for=\"name1\">Class:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"name1\" name=\"name\" value=\"java.lang.System\"><br><br>\r\n" + //
                        "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsgClass()\">\r\n" + //
                        "    </form>\r\n" + //
                        "    <div id=\"getrespmsg\"></div>\r\n" + //
                        "\r\n" + //
                        "    <script>\r\n" + //
                        "        function loadGetMsgClass() {\r\n" + //
                        "            let nameVar = document.getElementById(\"name1\").value;\r\n" + //
                        "            const xhttp = new XMLHttpRequest();\r\n" + //
                        "            xhttp.onload = function () {\r\n" + //
                        "                document.getElementById(\"getrespmsg\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "            }\r\n" + //
                        "            xhttp.open(\"GET\", \"http://localhost:45000/consulta?comando=Class(\" + nameVar + \")\");\r\n" + //
                        "            xhttp.send();\r\n" + //
                        "        }\r\n" + //
                        "    </script>\r\n" + //
                        "\r\n" + //
                        "    <h1>Form to invoke</h1>\r\n" + //
                        "    <form action=\"/invoke\">\r\n" + //
                        "        <label for=\"name\">invoke:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"name2\" name=\"name\" value=\"java.lang.System\"><br><br>\r\n" + //
                        "        <label for=\"name\">method:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"method2\" name=\"method\" value=\"getenv\"><br><br>\r\n" + //
                        "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetEnv()\">\r\n" + //
                        "    </form>\r\n" + //
                        "    <div id=\"getrespmsg1\"></div>\r\n" + //
                        "\r\n" + //
                        "    <script>\r\n" + //
                        "        function loadGetEnv() {\r\n" + //
                        "            let nameVar = document.getElementById(\"name2\").value;\r\n" + //
                        "            let method = document.getElementById(\"method2\").value;\r\n" + //
                        "            const xhttp = new XMLHttpRequest();\r\n" + //
                        "            xhttp.onload = function () {\r\n" + //
                        "                document.getElementById(\"getrespmsg1\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "            }\r\n" + //
                        "            xhttp.open(\"GET\", \"http://localhost:45000/consulta?comando=invoke(\" + nameVar + \",\" + method + \")\");\r\n" + //
                        "            xhttp.send();\r\n" + //
                        "        }\r\n" + //
                        "    </script>\r\n" + //
                        "\r\n" + //
                        "    <h1>Form to unary</h1>\r\n" + //
                        "    <form action=\"/unaryInvoke\">\r\n" + //
                        "        <label for=\"name\">invoke:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"name3\" name=\"name\" value=\"java.lang.Math\"><br><br>\r\n" + //
                        "        <label for=\"method\">method:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"method3\" name=\"method\" value=\"abs\"><br><br>\r\n" + //
                        "        <label for=\"type1\">type:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"type31\" name=\"type1\" value=\"double\"><br><br>\r\n" + //
                        "        <label for=\"value1\">value:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"value31\" name=\"value1\" value=\"-4.5\"><br><br>\r\n" + //
                        "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetUnary()\">\r\n" + //
                        "    </form>\r\n" + //
                        "    <div id=\"getrespmsg2\"></div>\r\n" + //
                        "\r\n" + //
                        "    <script>\r\n" + //
                        "        function loadGetUnary() {\r\n" + //
                        "            let nameVar = document.getElementById(\"name3\").value;\r\n" + //
                        "            let method = document.getElementById(\"method3\").value;\r\n" + //
                        "            let type1 = document.getElementById(\"type31\").value;\r\n" + //
                        "            let value1 = document.getElementById(\"value31\").value;\r\n" + //
                        "            const xhttp = new XMLHttpRequest();\r\n" + //
                        "            xhttp.onload = function () {\r\n" + //
                        "                document.getElementById(\"getrespmsg2\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "            }\r\n" + //
                        "            xhttp.open(\"GET\", \"http://localhost:45000/consulta?comando=unaryInvoke(\" + nameVar + \",\" + method + \",\" + type1 + \",\" + value1 + \")\");\r\n" + //
                        "            xhttp.send();\r\n" + //
                        "        }\r\n" + //
                        "    </script>\r\n" + //
                        "\r\n" + //
                        "    <h1>Form to binary</h1>\r\n" + //
                        "    <form action=\"/binaryInvoke\">\r\n" + //
                        "        <label for=\"name\">invoke:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"name4\" name=\"name\" value=\"java.lang.Math\"><br><br>\r\n" + //
                        "        <label for=\"method\">method:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"method4\" name=\"method\" value=\"max\"><br><br>\r\n" + //
                        "        <label for=\"type1\">type1:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"type41\" name=\"type1\" value=\"double\"><br><br>\r\n" + //
                        "        <label for=\"value1\">value1:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"value41\" name=\"value1\" value=\"-4.5\"><br><br>\r\n" + //
                        "        <label for=\"type2\">type2:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"type42\" name=\"type2\" value=\"double\"><br><br>\r\n" + //
                        "        <label for=\"value2\">value2:</label><br>\r\n" + //
                        "        <input type=\"text\" id=\"value42\" name=\"value2\" value=\"-4.5\"><br><br>\r\n" + //
                        "        <input type=\"button\" value=\"Submit\" onclick=\"loadGetBinary()\">\r\n" + //
                        "    </form>\r\n" + //
                        "    <div id=\"getrespmsg3\"></div>\r\n" + //
                        "\r\n" + //
                        "    <script>\r\n" + //
                        "        function loadGetBinary() {\r\n" + //
                        "            let nameVar = document.getElementById(\"name4\").value;\r\n" + //
                        "            let method = document.getElementById(\"method4\").value;\r\n" + //
                        "            let type1 = document.getElementById(\"type41\").value;\r\n" + //
                        "            let value1 = document.getElementById(\"value41\").value;\r\n" + //
                        "            let type2 = document.getElementById(\"type42\").value;\r\n" + //
                        "            let value2 = document.getElementById(\"value42\").value;\r\n" + //
                        "            const xhttp = new XMLHttpRequest();\r\n" + //
                        "            xhttp.onload = function () {\r\n" + //
                        "                document.getElementById(\"getrespmsg3\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "            }\r\n" + //
                        "            xhttp.open(\"GET\", \"http://localhost:45000/consulta?comando=binaryInvoke(\" + nameVar + \",\" + method + \",\" + type1 + \",\" + value1 +\",\" + type2 + \",\" + value2+ \")\");\r\n" + //
                        "            xhttp.send();\r\n" + //
                        "        }\r\n" + //
                        "    </script>\r\n" + //
                        "</body>\r\n" + //
                        "\r\n" + //
                        "</html>";
    }
}
