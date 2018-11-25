package com.example.linxl.circle.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Linxl on 2018/11/11.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, RequestBody requestBody, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
