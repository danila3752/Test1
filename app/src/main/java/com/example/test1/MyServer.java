package com.example.test1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {
    public ScanUrovo scanUrovo;
    public Context context;
    public RFIDReader rfidReader;
    public List<String> Result1;
    public List<String> Result2;
    public MyServer(Context context) {
        super(8080);
        // Инициализация объекта rfidReader
        rfidReader = new RFIDReader(context);
        this.context = context;
    }
    private String tagData;
    private Response tagResponse;

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if ("/api/data".equals(uri)) {
            if (Method.GET.equals(session.getMethod())) {
                try {
                    tagData = rfidReader.readTags();
                } catch (InterruptedException e) {
                    Log.e("error", e.getMessage());
                }
                if (tagData != null) {
                    // Возвращаем ответ с данными метки RFID
                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, tagData);
                } else {
                    // Возвращаем ответ с сообщением об ошибке
                    return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "No tag data...");
                }

            } else if (Method.POST.equals(session.getMethod())) {
                try {
                    Map<String, String> files = new HashMap<>();
                    session.parseBody(files);


                    String value = files.get("postData");
                    Boolean WriteResult=rfidReader.writeTag(value);
                    if(WriteResult==true){
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "Success");
                    }else {
                        return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "Error");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }else if("/api/inventory".equals(uri)){
            try {
                Result1 = rfidReader.readInventoryTags();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Result1 != null) {
                // Возвращаем ответ с данными метки RFID
                String responseString = String.join(",", Result1);
                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, responseString);
            } else {
                // Возвращаем ответ с сообщением об ошибке
                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "No tag data...");
            }
        }else if("/api/stopInventory".equals(uri)){
            try {
                Result2 = rfidReader.StopInventory();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Result2 != null) {
                // Возвращаем ответ с данными метки RFID
                String responseString = String.join(",", Result2);
                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, responseString);
            } else {
                // Возвращаем ответ с сообщением об ошибке
                return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "No tag data...");
            }
        }

        // Возвращаем сохраненный ответ или ответ "Not Found" по умолчанию
        return tagResponse != null ? tagResponse : newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }





}