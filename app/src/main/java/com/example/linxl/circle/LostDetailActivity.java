package com.example.linxl.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.LostItem;
import com.example.linxl.circle.gson.ViewPointItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;
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

public class LostDetailActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView userName;
    private TextView sendTime;
    private TextView content;
    private TextView lostTime;
    private TextView lostLocation;
    private TextView lostContact;
    private ImageButton commentButton;
    private ImageButton connectButton;
    private RecyclerView images;
    private RecyclerView viewPoint;

    private ImageAdapter mImageAdapter;
    private ViewPointAdapter mViewPointAdapter;

    private LostItem mLostItem;
    private List<String> imgPaths;
    private List<ViewPointItem> mViewPointItems;

    private String myId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_detail);
        mCircleImageView = (CircleImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        sendTime = (TextView) findViewById(R.id.send_time);
        content = (TextView) findViewById(R.id.lost_content);
        lostTime = (TextView) findViewById(R.id.lost_time);
        lostLocation = (TextView) findViewById(R.id.lost_location);
        lostContact = (TextView) findViewById(R.id.lost_contact);
        commentButton = (ImageButton) findViewById(R.id.button_comment);
        connectButton = (ImageButton) findViewById(R.id.button_connect);
        images = (RecyclerView) findViewById(R.id.lost_images);
        viewPoint = (RecyclerView) findViewById(R.id.view_point);
        imgPaths = new ArrayList<>();
        mViewPointItems = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        Intent intent = getIntent();
        String keyId = intent.getStringExtra("keyId");
        String label = intent.getStringExtra("label");
        userId = intent.getStringExtra("userId");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(LostDetailActivity.this, 3);
        mImageAdapter = new ImageAdapter(imgPaths);
        images.setLayoutManager(gridLayoutManager);
        images.setAdapter(mImageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mViewPointAdapter = new ViewPointAdapter(mViewPointItems);
        viewPoint.setLayoutManager(linearLayoutManager);
        viewPoint.setAdapter(mViewPointAdapter);

        String address = getString(R.string.server_ip) + "itemDetailServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("keyId", keyId)
                .add("label", label)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "内容加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    mLostItem = gson.fromJson(responseData, LostItem.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userName.setText(mLostItem.getUserName());
                            sendTime.setText(mLostItem.getSendTime());
                            content.setText(mLostItem.getContent());
                            lostTime.setText(mLostItem.getEventTime());
                            lostContact.setText(mLostItem.getContact());
                            Glide.with(LostDetailActivity.this).load(getString(R.string.server_ip) + "image/user_img/" + mLostItem.getUserImg()).into(mCircleImageView);

                            if (!mLostItem.getLostImgs().isEmpty()) {
                                for (String imgPath : mLostItem.getLostImgs()) {
                                    imgPaths.add(getString(R.string.server_ip) + "image/" + mLostItem.getUserId() + "/" + imgPath);
                                }
                            }

                            if (mLostItem.getUserId().equals(myId)){
                                connectButton.setBackgroundResource(R.drawable.ic_connect_unable);
                                connectButton.setEnabled(false);
                            }else {
                                connectButton.setBackgroundResource(R.drawable.ic_connect);
                                connectButton.setEnabled(true);
                            }

                            mImageAdapter.notifyDataSetChanged();
                            invalidateOptionsMenu();
                        }
                    });
                }
            }
        });

        address = getString(R.string.server_ip) + "viewPointServlet";
        requestBody = new FormBody.Builder()
                .add("keyId", keyId)
                .add("label", label)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "评论加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    if (responseData.equals("NoData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LostDetailActivity.this, "暂时没有评论", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Gson gson = new Gson();
                        mViewPointItems = gson.fromJson(responseData,
                                new TypeToken<List<ViewPointItem>>() {
                                }.getType());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mViewPointAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText content = new EditText(LostDetailActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(LostDetailActivity.this);
                builder.setView(content);
                builder.setPositiveButton("发表", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = getString(R.string.server_ip) + "newViewPointServlet";
                        RequestBody requestBody = new FormBody.Builder()
                                .add("keyId", mLostItem.getLostId())
                                .add("label", "Lost")
                                .add("toId", mLostItem.getUserId())
                                .add("content", content.getText().toString())
                                .add("userId", (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, ""))
                                .add("sendTime", TimeCapture.getChinaTime())
                                .build();
                        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LostDetailActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(LostDetailActivity.this, responseData, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                builder.show();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostDetailActivity.this, ChatActivity.class);
                intent.putExtra("fromId", (String) SPUtil.getParam(LostDetailActivity.this, SPUtil.USER_ID, ""));
                intent.putExtra("toId", mLostItem.getUserId());
                intent.putExtra("contactName", mLostItem.getUserName());
                intent.putExtra("contactImg", mLostItem.getUserImg());
                startActivity(intent);
            }
        });

        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostDetailActivity.this, UserCardActivity.class);
                intent.putExtra("userId", mLostItem.getUserId());
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_popup, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (myId.equals(userId)){
            menu.findItem(R.id.delete).setVisible(true);
            if (mLostItem != null && mLostItem.isFlag()){
                menu.findItem(R.id.hide).setVisible(false);
                menu.findItem(R.id.open).setVisible(true);
            }else {
                menu.findItem(R.id.hide).setVisible(true);
                menu.findItem(R.id.open).setVisible(false);
            }
        }else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.hide).setVisible(false);
            menu.findItem(R.id.open).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete:
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setMessage("删除后将无法恢复，点击确定删除");
                dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMyLost(mLostItem.getUserId(), mLostItem.getSendTime());
                    }
                });
                dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog1.show();
                break;
            case R.id.hide:
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
                dialog2.setMessage("点击确定，将问题设为仅自己可见");
                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideMyLost(mLostItem.getUserId(), mLostItem.getSendTime());
                    }
                });
                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog2.show();
                break;
            case R.id.open:
                AlertDialog.Builder dialog3 = new AlertDialog.Builder(this);
                dialog3.setMessage("点击确定，将问题设为公开");
                dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openMyLost(mLostItem.getUserId(), mLostItem.getSendTime());
                    }
                });
                dialog3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog3.show();
                break;
            case R.id.like:
                final EditText name = new EditText(this);
                AlertDialog.Builder dialog4 = new android.support.v7.app.AlertDialog.Builder(this);
                dialog4.setView(name);
                dialog4.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCollection(name.getText().toString(), mLostItem.getUserId(), mLostItem.getLostId());
                    }
                });
                dialog4.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog4.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void deleteMyLost(String userId, String sendTime) {
        String address = getString(R.string.server_ip) + "deleteLostServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LostDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void hideMyLost(String userId, String sendTime) {
        String address = getString(R.string.server_ip) + "updateLostFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .add("flag", "true")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LostDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void openMyLost(String userId, String sendTime) {
        String address = getString(R.string.server_ip) + "updateLostFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .add("flag", "false")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LostDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void addCollection(String name, String userId, String keyId) {
        String sendTime = TimeCapture.getChinaTime();
        String address = getString(R.string.server_ip) + "newCollectionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("userId", userId)
                .add("collectionTime", sendTime)
                .add("keyId", keyId)
                .add("label", "Lost")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LostDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
