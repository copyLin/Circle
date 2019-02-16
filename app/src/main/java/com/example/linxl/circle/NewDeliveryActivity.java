package com.example.linxl.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.ImageLoader;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewDeliveryActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;
    private ProgressBar mProgressBar;
    private EditText content;
    private EditText price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        content = (EditText) findViewById(R.id.delivery_content);
        price = (EditText) findViewById(R.id.delivery_price);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                mProgressBar.setVisibility(View.VISIBLE);

                String userId = (String) SPUtil.getParam(this, SPUtil.USER_ID, "");
                String deliveryContent = content.getText().toString();
                String deliveryPrice = price.getText().toString();
                String sendTime = TimeCapture.getChinaTime();

                mProgressBar.setProgress(50);

                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", userId)
                        .add("content", deliveryContent)
                        .add("price", deliveryPrice)
                        .add("sendTime", sendTime)
                        .build();
                String address = getString(R.string.server_ip) + "newDeliveryServlet";

                HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        NewDeliveryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(100);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(NewDeliveryActivity.this, "信息发送失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            NewDeliveryActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(100);
                                    Toast.makeText(NewDeliveryActivity.this, "信息发送成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }

                    }
                });
                break;
            default:
                break;
        }
        return true;
    }
}
