package com.example.linxl.circle;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.UserItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserCardActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView userName;
    private TextView userId;
    private TextView userDepartment;
    private TextView userMajor;
    private TextView userWords;

    private UserItem mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card);

        mCircleImageView= (CircleImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        userId = (TextView) findViewById(R.id.user_id);
        userDepartment = (TextView) findViewById(R.id.user_department);
        userMajor = (TextView) findViewById(R.id.user_major);
        userWords= (TextView) findViewById(R.id.user_words);
        Button sendButton = (Button) findViewById(R.id.button_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        Intent intent = getIntent();
        String id = intent.getStringExtra("userId");

        String address = getString(R.string.server_ip) + "getUserInformation";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", id)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserCardActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Gson gson = new Gson();
                    mUser = gson.fromJson(responseData, UserItem.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(UserCardActivity.this).load(getString(R.string.server_ip) + "image/user_img/" + mUser.getUserImg()).into(mCircleImageView);
                            userId.setText(mUser.getUserId());
                            userName.setText(mUser.getUserName());
                            userDepartment.setText(mUser.getDepartment());
                            userMajor.setText(mUser.getMajor());
                            userWords.setText(mUser.getWords());
                        }
                    });
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
