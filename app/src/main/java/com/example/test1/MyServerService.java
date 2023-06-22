package com.example.test1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MyServerService extends Service {
    private MyServer server;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        server = new MyServer(getApplicationContext());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Создание уведомления для фоновой службы
        createNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop();
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {
        // Создание канала уведомлений (для Android 8.0 и выше)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Создание уведомления
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Сервер активен")
                .setContentText("Ваш сервер находится в фоновом режиме")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        // Запуск службы в фоновом режиме
        startForeground(NOTIFICATION_ID, builder.build());
    }
}
