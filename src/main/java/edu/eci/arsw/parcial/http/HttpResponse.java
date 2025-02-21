/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arsw.parcial.http;

/**
 *
 * @author mateo.forero-f
 */
public class HttpResponse {

    private int statusCode;
    private String contentType;
    private String body;

    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }
    public void setContentType(String contentType){
        this.contentType = contentType;
    }
    public void setBody(String body){
        this.body = body;
    }

    public String toString() {
        String outputLine = "HTTP/1.1 "+statusCode+" OK\r\n"
                + "Content-Type: "+contentType+"\r\n"
                + "\r\n"
                + body;
        return outputLine;
    }

}
