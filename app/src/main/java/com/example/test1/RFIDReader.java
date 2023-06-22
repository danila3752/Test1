package com.example.test1;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import fi.iki.elonen.NanoHTTPD;

public class RFIDReader {

    public RFIDReader(Context context) {
        this.curContext = context;
    }

    public Context curContext;
    public  ScanUrovo scanUrovo;

    public String readTags() throws InterruptedException {
        // Ваш код чтения RFID меток
//        curContext = scanUrovo.getApplicationContext();
        scanUrovo = new ScanUrovo();
        scanUrovo.initRfidUrovo(curContext);
        Thread.sleep(2200);
        String tagData = scanUrovo.ReadOneTagUrovo(curContext);
        // После чтения метки, вызывайте responseCallback для отправки ответа
       return tagData;
    }

    public void writeTag(String data) {
        // Код для записи данных на метку
    }
}
