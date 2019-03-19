package com.example.linxl.circle;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatService extends Service {

    private static final String HOST = "10.29.17.3";
    private static final int PORT = 8090;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    private Socket socket = null;
    private BufferedReader in;
    private BufferedWriter out;
    private Gson mGson = new Gson();

    private boolean flag = true;

    private ChatBinder mBinder = new ChatBinder();
    private LocalBroadcastManager mLocalBroadcastManager;

    class ChatBinder extends Binder {
        public void sendMessage(final ChatItem item){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isConnected(socket)){
                            String jsonData  = mGson.toJson(item);
                            out.write(jsonData + "\n");
                            out.flush();

                            Intent intent = new Intent("com.example.linxl.circle.NEW_MESSAGE");
                            intent.putExtra("new_msg", item);
                            mLocalBroadcastManager.sendBroadcast(intent);

                            Log.d("———ChatService———", "jsonData" + jsonData);
                        } else {
                            Intent intent = new Intent("com.example.linxl.circle.SOCKET_RECONNECT");
                            mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void reconnect() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(HOST, PORT);
                        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                        if (isConnected(socket)){
                            out.write("enter:" + userId + "\n");
                            out.flush();
                        }else {
                            Intent intent = new Intent("com.example.linxl.circle.SOCKET_RECONNECT");
                            mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection();
                try {
                    String jsonData;
                    while (flag) {
                        if ((jsonData = in.readLine()) != null) {

                            ChatItem item = mGson.fromJson(jsonData, ChatItem.class);
                            item.save();

                            Intent intent = new Intent("com.example.linxl.circle.NEW_MESSAGE");
                            intent.putExtra("new_msg", item);
                            mLocalBroadcastManager.sendBroadcast(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        flags = START_REDELIVER_INTENT;
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (isConnected(socket)) {
                        out.write("exit:" + userId + "\n");
                        out.flush();
                    }

                    flag = false;

                    if (in != null){
                        in.close();
                    }
                    if (out != null){
                        out.close();
                    }
                    if (socket != null){
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        super.onDestroy();
    }


    /**
     * 连接服务器
     */
    private void connection() {
        try {
            socket = new Socket(HOST, PORT);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            if (isConnected(socket)){
                out.write("enter:" + userId + "\n");
                out.flush();
            }else {
                Intent intent = new Intent("com.example.linxl.circle.SOCKET_RECONNECT");
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Boolean isConnected(Socket socket){
        try{
            socket.sendUrgentData(0xFF);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
