/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arsw.parcial.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mateo.forero-f
 */
public class HttpRequest {

    /*
     * GET / HTTP/1.1
     * User-Agent: PostmanRuntime/7.37.3
     * Accept:
     * Postman-Token:4960f 4 af-6d fc-4 ae3-85f 8-fcd9e6498a06
     * Host:localhost:45000 Accept-Encoding:gzip,deflate,
     * br
     * Connection:keep-alive
     */

    private URI uri;
    private Map<String,String> queryParams;

    public HttpRequest(String request) throws URISyntaxException {
        if (request== null || request.equals("")) throw new IllegalArgumentException();
        String req = request.split("\n")[0];
        String processReq = req.split("\s")[1];
        uri =  new URI(processReq);
        queryParams = new HashMap<String,String>();
        if (uri.getQuery()!=null){
            String[] query = uri.getQuery().trim().split("&");
            
            for (String q: query){
                String[] parts = q.split("=");
                String name = parts[0];
                String value =  parts[1];
                queryParams.put(name, value);
            }
        }
    }

    public URI getUri(){
        return uri;
    }

    public String getASpecificParam(String param){
        return queryParams.get(param);
    }

}
