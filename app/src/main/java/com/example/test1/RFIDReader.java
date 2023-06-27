package com.example.test1;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import fi.iki.elonen.NanoHTTPD;

public class RFIDReader {

    public RFIDReader(Context context) {
        this.curContext = context;
    }

    public Context curContext;
    public  ScanUrovo scanUrovo;
    public WriteUrovo writeUrovo;

    public String readTags() throws InterruptedException {
        // Ваш код чтения RFID меток
//        curContext = scanUrovo.getApplicationContext();
        scanUrovo = new ScanUrovo();
        scanUrovo.initRfidUrovo(curContext);
        Thread.sleep(2300);
        String tagData = scanUrovo.ReadOneTagUrovo(curContext);
        return tagData;
    }

    public List<String> readInventoryTags() throws InterruptedException {
        scanUrovo = new ScanUrovo();
        scanUrovo.initRfidUrovo(curContext);
        Thread.sleep(2300);
        List<String> scanResult=scanUrovo.ReadInventory(curContext,true);
        return  scanResult;
    }
    public List<String> StopInventory() throws InterruptedException {
        List<String> scanResult=scanUrovo.ReadInventory(curContext,false);
        return  scanResult;
    }

    public Boolean writeTag(String data) throws InterruptedException {
        writeUrovo=new WriteUrovo();
        writeUrovo.initRfidUrovo(curContext);
        Thread.sleep(2300);
        Boolean WriteResult=writeUrovo.WriteRFID(data);
        // Код для записи данных на метку
        return WriteResult;
    }
}
