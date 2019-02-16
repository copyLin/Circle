package com.example.linxl.circle;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.linxl.circle.utils.SPUtil;

public class WelcomeActivity extends AppCompatActivity {

    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
