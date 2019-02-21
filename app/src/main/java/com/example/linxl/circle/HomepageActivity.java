package com.example.linxl.circle;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.UserItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomepageActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView userId;
    private TextView userName;
    private TextView userDepartment;
    private TextView userMajor;
    private TextView userWords;
    private Button changePassword;
    private FloatingActionButton editInfo;
    private UserItem mUser;

    private String id = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mCircleImageView = (CircleImageView) findViewById(R.id.user_image);
        userId = (TextView) findViewById(R.id.user_id);
        userName = (TextView) findViewById(R.id.user_name);
        userDepartment = (TextView) findViewById(R.id.user_department);
        userMajor = (TextView) findViewById(R.id.user_major);
        userWords = (TextView) findViewById(R.id.user_words);
        changePassword = (Button) findViewById(R.id.change_password);
        editInfo = (FloatingActionButton) findViewById(R.id.button_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        String address = getString(R.string.server_ip) + "userInformation";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", id)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(HomepageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
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
                            Glide.with(HomepageActivity.this).load(getString(R.string.server_ip) + "image/user_img/" + mUser.getUserImg()).into(mCircleImageView);
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

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText oldPassword = new EditText(HomepageActivity.this);
                final EditText newPassword1 = new EditText(HomepageActivity.this);
                final EditText newPassword2 = new EditText(HomepageActivity.this);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(HomepageActivity.this);
                dialog.setView(oldPassword);
                dialog.setView(newPassword1);
                dialog.setView(newPassword2);
                dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = getString(R.string.server_ip) + "changePassword";
                        RequestBody requestBody = new FormBody.Builder()
                                .add("userId", id)
                                .add("oldPassword", oldPassword.getText().toString())
                                .add("newPassword1", newPassword1.getText().toString())
                                .add("newPassword2", newPassword2.getText().toString())
                                .build();
                        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(HomepageActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()){
                                    final String responseData = response.body().string();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(HomepageActivity.this, responseData, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
