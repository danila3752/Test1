package com.example.test1;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {
    public ScanUrovo scanUrovo;
    public Context context;
    public RFIDReader rfidReader;

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
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error reading RFID tag");
                }


                // Обработка GET-запроса на эндпоинт '/api/data'
//                RFIDReadCallback readCallback = new RFIDReadCallback() {
//                    @Override
//                    public void onTagRead(String tagData, ResponseCallback responseCallback) {
//                        // Отправляем ответ после чтения метки
//                        tagResponse = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, tagData);
//                    }
//                };

//                rfidReader.readTags(readCallback, new ResponseCallback() {
//                    @Override
//                    public void onResponse(Response response) {
//                        // Сохраняем ответ для последующего возврата
//                        tagResponse = response;
//                    }
//                });

                // Возвращаем временный ответ с сообщением "Reading RFID tag..."
              //  return newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "Reading RFID tag...");
            } else if (Method.POST.equals(session.getMethod())) {
                // Обработка POST-запроса на эндпоинт '/api/data'
                // Ваш код обработки POST-запроса
            }
        }

        // Возвращаем сохраненный ответ или ответ "Not Found" по умолчанию
        return tagResponse != null ? tagResponse : newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }
}