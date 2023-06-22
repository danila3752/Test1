package com.example.test1;

import fi.iki.elonen.NanoHTTPD;

public interface RFIDReadCallback {
    void onTagRead(String tagData, ResponseCallback responseCallback);
}
