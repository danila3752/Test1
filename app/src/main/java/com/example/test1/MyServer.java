package com.example.test1;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {
    private RequestHandler requestHandler;

    public MyServer() {
        super(8080); // Устанавливаем порт, на котором будет работать сервер
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if ("/api/data".equals(uri)) {
            if (Method.GET.equals(session.getMethod())) {
                // Обработка GET-запроса на эндпоинт '/api/data'

                String responseData = "Data retrieved successfully";
                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, responseData);
            } else if (Method.POST.equals(session.getMethod())) {
                // Обработка POST-запроса на эндпоинт '/api/data'
                try {
                    Map<String, String> files = new HashMap<>();
                    session.parseBody(files);
                    String requestData = session.getQueryParameterString();
                    // Обработка данных
                    String responseData = "Data processed successfully";

                    if (requestHandler != null) {
                        requestHandler.handleRequest(responseData);
                    }

                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, responseData);
                } catch (IOException | ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error");
                }
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }

    
}
