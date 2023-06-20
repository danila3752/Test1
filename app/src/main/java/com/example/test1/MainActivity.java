package com.example.test1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RequestHandler {
    private MyServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server = new MyServer();
        server.setRequestHandler(this);

        // Запуск сервера
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Остановка сервера при закрытии Activity
        if (server != null) {
            server.stop();
        }
    }

    // Реализация метода handleRequest()
    @Override
    public void handleRequest(String data) {
        // Обработка полученных данных и переход в нужный layout
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Ваш код для перехода в нужный layout
                setContentView(R.layout.show_scan_ver);
            }
        });
    }
}