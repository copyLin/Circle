package com.example.linxl.circle;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

    private static final String HOST = "192.168.31.100";
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
                        if (socket.isConnected()) {
                            if (!socket.isOutputShutdown()) {
                                String jsonData  = mGson.toJson(item);
                                out.write(jsonData);

                                Intent intent = new Intent("com.example.linxl.gduf_im.NEW_MESSAGE");
                                intent.putExtra("new_msg", item);
                                mLocalBroadcastManager.sendBroadcast(intent);
                            } else {
                                Log.d("———ChatService———", "输出流断开");
                            }
                        } else {
                            Log.d("———ChatService———", "socket连接断开");
                        }
                    } catch (IOException e) {
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
                        if (!socket.isClosed()) {
                            if (socket.isConnected()) {
                                if (!socket.isInputShutdown()) {
                                    if ((jsonData = in.readLine()) != null) {

                                        ChatItem item = mGson.fromJson(jsonData, ChatItem.class);
                                        item.save();

                                        Intent intent = new Intent("com.example.linxl.gduf_im.NEW_MESSAGE");
                                        intent.putExtra("new_msg", item);
                                        mLocalBroadcastManager.sendBroadcast(intent);

                                    }
                                } else {
                                    Log.d("———ChatService———", "输入流断开");
                                }
                            } else {
                                Log.d("———ChatService———", "socket连接断开");
                            }
                        } else {
                            Log.d("———ChatService———", "socket关闭");
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
                    if (socket != null) {
                        if (!socket.isOutputShutdown()) {
                            out.write("exit:" + userId);
                            out.flush();
                        }
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
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.write("enter:" + userId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
