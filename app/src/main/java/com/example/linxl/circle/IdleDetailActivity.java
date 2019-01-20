package com.example.linxl.circle;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.CommentItem;
import com.example.linxl.circle.gson.IdleItem;
import com.example.linxl.circle.gson.LostItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IdleDetailActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView userName;
    private TextView sendTime;
    private TextView idleName;
    private TextView content;
    private TextView idlePrice;
    private ImageButton commentButton;
    private ImageButton connectButton;
    private RecyclerView images;
    private RecyclerView comments;

    private ImageAdapter mImageAdapter;
    private CommentAdapter mCommentAdapter;

    private List<CommentItem> mCommentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle_detail);
        mCircleImageView = (CircleImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        sendTime = (TextView) findViewById(R.id.send_time);
        idleName = (TextView) findViewById(R.id.idle_name);
        content = (TextView) findViewById(R.id.idle_content);
        idlePrice = (TextView) findViewById(R.id.idle_price);
        commentButton = (ImageButton) findViewById(R.id.button_comment);
        connectButton = (ImageButton) findViewById(R.id.button_connect);
        images = (RecyclerView) findViewById(R.id.idle_images);
        comments = (RecyclerView) findViewById(R.id.idle_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        Intent intent = getIntent();
        final IdleItem item = (IdleItem) intent.getSerializableExtra("item");
        userName.setText(item.getUserName());
        sendTime.setText(item.getSendTime());
        idleName.setText(item.getIdleName());
        content.setText(item.getContent());
        idlePrice.setText(item.getPrice());
        Glide.with(this).load(R.string.server_ip + "user_img/" + item.getUserImg()).into(mCircleImageView);

        List<String> imgPaths = new ArrayList<>();
        for (String imgPath : item.getIdleImgs()){
            imgPaths.add(R.string.server_ip + "image/" + item.getUserId() + "/" + imgPath);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mImageAdapter = new ImageAdapter(imgPaths);
        images.setLayoutManager(gridLayoutManager);
        images.setAdapter(mImageAdapter);

        String address = R.string.server_ip + "commentServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", item.getUserId())
                .add("sendTime", item.getSendTime())
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IdleDetailActivity.this, "评论加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    mCommentItems = gson.fromJson(responseData,
                            new TypeToken<List<LostItem>>(){}.getType());
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mCommentAdapter = new CommentAdapter(mCommentItems);
        comments.setLayoutManager(linearLayoutManager);
        comments.setAdapter(mCommentAdapter);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdleDetailActivity.this, ChatActivity.class);
                intent.putExtra("fromId", (String) SPUtil.getParam(IdleDetailActivity.this, SPUtil.USER_ID, ""));
                intent.putExtra("toId", item.getUserId());
                intent.putExtra("contactImg", item.getUserImg());
                startActivity(intent);
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
