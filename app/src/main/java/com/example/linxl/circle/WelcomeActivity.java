package com.example.linxl.circle;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.linxl.circle.gson.CollectionItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity {

    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_welcome);
        isLogin = (Boolean) SPUtil.getParam(this, SPUtil.IS_LOGIN, false);
        mHandler.sendEmptyMessageDelayed(0,3000);

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome() {
        if (isLogin){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }

}
