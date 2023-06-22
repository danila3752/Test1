package com.example.test1;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;

import fi.iki.elonen.NanoHTTPD;

public class RFIDReader {

    public RFIDReader(Context context) {
        this.curContext = context;
    }

    public Context curContext;
    public  ScanUrovo scanUrovo;
    public void readTags(RFIDReadCallback readCallback, ResponseCallback responseCallback) {
        // Ваш код чтения RFID меток
//        curContext = scanUrovo.getApplicationContext();
        scanUrovo = new ScanUrovo();
        scanUrovo.initRfidUrovo(curContext);
        //  scanUrovo.ReadOneTagUrovo(curContext);
        // После чтения метки, вызывайте responseCallback для отправки ответа
        String tagData = "rgrehthh";
        NanoHTTPD.Response response = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MIME_PLAINTEXT, tagData);
        responseCallback.onResponse(response);
    }

    public void writeTag(String data) {
        // Код для записи данных на метку
    }
}
