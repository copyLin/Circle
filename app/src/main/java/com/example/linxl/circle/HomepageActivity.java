package com.example.linxl.circle;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        editInfo = (FloatingActionButton) findViewById(R.id.button_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        getUserInformation();

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.info_layout, new EditInfoFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                editInfo.setImageResource(R.drawable.ic_send);
            }
        });

        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, UserImageActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_homepage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.setting:
                startActivity(new Intent(HomepageActivity.this, SettingActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void getUserInformation(){
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
    }
}
