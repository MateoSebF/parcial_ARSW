package edu.eci.arsw.parcial.http;

import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

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
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println(request);
            HttpRequest httpRequest;
            HttpResponse httpResponse = new HttpResponse();
            try {
                // http://localhost:35000/compreflex?comando=binaryInvoke(java.lang.Math, max,
                // double, 4.5, double, -3.7)
                httpRequest = new HttpRequest(request);
                if (httpRequest.getUri().getPath().equals("/compreflex")) {
                    httpResponse = manageRequest(httpRequest);
                } else {
                    httpResponse.setStatusCode(400);
                    httpResponse.setContentType("application/json");
                    httpResponse.setBody("");
                }

            } catch (Exception e) {
                e.printStackTrace();
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

    public static HttpResponse manageRequest(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse();
        HashMap<String, String> params = new HashMap<>();
        String petition = httpRequest.getASpecificParam("comando");
        String comando = petition.split("\\(")[0];
        String paramS = petition.split("\\(")[1].substring(0, petition.split("\\(")[1].length() - 1);
        System.out.println(comando);
        System.out.println(paramS);
        String body;
        try {

            String[] listParams = paramS.split(",");
            if (comando.equals("Class")) {
                httpResponse.setStatusCode(200);
                httpResponse.setContentType("application/json");
                body = getClassParams(listParams[0]);
            } else if (comando.equals("invoke")) {
                httpResponse.setStatusCode(200);
                httpResponse.setContentType("application/json");
                body = makeInvokeNoParams(listParams[0], listParams[1]);
            } else if (comando.equals("unaryInvoke")) {
                httpResponse.setStatusCode(200);
                httpResponse.setContentType("application/json");
                body = makeInvokeWithOneParam(listParams[0], listParams[1], listParams[2], listParams[3]);
            } else if (comando.equals("binaryInvoke")) {
                httpResponse.setStatusCode(200);
                httpResponse.setContentType("application/json");
                body = makeInvokeWithTwoParam(listParams[0], listParams[1], listParams[2], listParams[3], listParams[4],
                        listParams[5]);
            } else {
                body = "{ error: \"This command not exist\"}";
            }
        } catch (Exception e) {
            body = "{ error: \"" + e.getMessage() + "\"}";
        }
        httpResponse.setBody(body);
        return httpResponse;
    }

    private static String makeInvokeWithTwoParam(String class1, String method, String type1, String value1,
            String type2, String value2) {
        String response = "{ \nresponse:\n";
        try {
            Class<?> classBase = Class.forName(class1);
            Class<?>[] types;
            Object[] objects;
            if (type1.equals("int")) {
                types = new Class[] { int.class };
                objects = new Object[] { (int) Integer.parseInt(value1) };
            } else if (type1.equals("double")) {
                types = new Class[] { double.class };
                objects = new Object[] { (double) Double.parseDouble(value1) };
            } else if (type1.equals("String")) {
                types = new Class[] { String.class };
                objects = new Object[] { value1 };
            } else {
                throw new IllegalArgumentException("No soprtamos el tipo" + type1);
            }

            if (type2.equals("int")) {
                types = new Class[] { types[0], int.class };
                objects = new Object[] { objects[0], (int) Integer.parseInt(value2) };
            } else if (type2.equals("double")) {
                types = new Class[] { types[0], double.class };
                objects = new Object[] { objects[0], (double) Double.parseDouble(value2) };
            } else if (type2.equals("String")) {
                types = new Class[] { types[0], String.class };
                objects = new Object[] { objects[0], value2 };
            } else {
                throw new IllegalArgumentException("No soprtamos el tipo" + type2);
            }
            Method callMethod = classBase.getDeclaredMethod(method, types);

            response += callMethod.invoke(null, objects);
        } catch (Exception e) {
            response += "\"" + e.getMessage() + "\"";
            e.printStackTrace();
        }
        response += "}";
        return response;
    }

    private static String makeInvokeWithOneParam(String class1, String method, String type1, String value1) {
        String response = "{ \nresponse:\n";
        try {
            Class<?> classBase = Class.forName(class1);
            Class<?>[] types;
            Object[] objects;
            if (type1.equals("int")) {
                types = new Class[] { int.class };
                objects = new Object[] { (int) Integer.parseInt(value1) };
            } else if (type1.equals("double")) {
                types = new Class[] { double.class };
                objects = new Object[] { (double) Double.parseDouble(value1) };
            } else if (type1.equals("String")) {
                types = new Class[] { String.class };
                objects = new Object[] { value1 };
            } else {
                throw new IllegalArgumentException("No soprtamos el tipo" + type1);
            }
            Method callMethod = classBase.getDeclaredMethod(method, types);

            response += callMethod.invoke(null, objects);
        } catch (Exception e) {
            response += "\"" + e.getMessage() + "\"";
            e.printStackTrace();
        }
        response += "}";
        return response;
    }

    private static String makeInvokeNoParams(String class1, String method) {
        String response = "{ \nresponse:\n";
        try {
            Class<?> classBase = Class.forName(class1);
            Method callMethod = classBase.getDeclaredMethod(method);
            response += callMethod.invoke(null);
        } catch (Exception e) {
            response += "\"" + e.getMessage() + "\"";
            e.printStackTrace();
        }
        response += "}";
        return response;
    }

    private static String getClassParams(String class1) {
        String response = "{ \nresponse:\n";
        try {
            Class<?> classBase = Class.forName(class1);
            List<Member> members = new LinkedList<>();
            for (Member m : classBase.getDeclaredMethods())
                members.add(m);
            for (Member m : classBase.getDeclaredFields())
                members.add(m);
            response += "[\n";
            for (int i = 0; i < members.size(); i++) {
                if (i < members.size() - 1) {
                    response += members.get(i) + ",\n";
                } else {
                    response += members.get(i) + "\n";
                }
            }
            response += "]\n";
        } catch (ClassNotFoundException e) {
            response += "\"" + e.getMessage() + "\"";
            e.printStackTrace();
        }
        response += "}";
        return response;
    }
}
