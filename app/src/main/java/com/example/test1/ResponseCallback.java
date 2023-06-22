package com.example.test1;

import fi.iki.elonen.NanoHTTPD;

public interface ResponseCallback {
    void onResponse(NanoHTTPD.Response response);
}