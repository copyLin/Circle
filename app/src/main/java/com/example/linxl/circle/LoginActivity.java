package com.example.linxl.circle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.linxl.circle.gson.UserItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userId;
    private EditText password;
    private ImageButton login;
    private UserItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_login);
        userId = (EditText) findViewById(R.id.user_id);
        password = (EditText) findViewById(R.id.password);
        login = (ImageButton) findViewById(R.id.button_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = getString(R.string.server_ip) + "loginServlet";
                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", userId.getText().toString())
                        .add("password", password.getText().toString())
                        .build();
                HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseData = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (responseData) {
                                        case "LoginSuccess":
                                            String id = userId.getText().toString();
                                            requestForUserInformation(id);
                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                            finish();
                                            break;
                                        case "WrongNumber":
                                            Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "NullError":
                                            Toast.makeText(LoginActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void requestForUserInformation(String userId){
        String address = getString(R.string.server_ip) + "userInformation";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    user = gson.fromJson(responseData, UserItem.class);
                    SPUtil.setParam(LoginActivity.this, SPUtil.USER_ID, user.getUserId());
                    SPUtil.setParam(LoginActivity.this, SPUtil.USER_NAME, user.getUserName());
                    SPUtil.setParam(LoginActivity.this, SPUtil.USER_IMG, user.getUserImg());
                    SPUtil.setParam(LoginActivity.this, SPUtil.IS_LOGIN, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }
}
