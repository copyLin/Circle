package com.example.linxl.circle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.TimeCapture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView inputText;
    private ImageButton send;

    private List<ChatItem> mChatItems;
    private ChatAdapter mChatAdapter;

    private ChatService.ChatBinder mChatBinder;
    private ChatReceiver mChatReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    private String fromId;
    private String toId;
    private String contactImg;
    private String currentId = "0";
    private int firstVisibleItem;
    private boolean hasMore = true;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mChatBinder = (ChatService.ChatBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        send = (ImageButton) findViewById(R.id.send);
        inputText = (TextView) findViewById(R.id.input_text);

        final Intent intent = getIntent();
        fromId = intent.getStringExtra("fromId");
        toId = intent.getStringExtra("toId");
        contactImg = intent.getStringExtra("contactImg");
        mChatItems = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        DataSupport.deleteAll(ChatItem.class, "fromId = ? and flag = ?", toId, "0");

        requestForChatRecord();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChatAdapter = new ChatAdapter(mChatItems, contactImg);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mChatAdapter);

        Intent serviceIntent = new Intent(this, ChatService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendTime = TimeCapture.getChinaTime();
                String content = inputText.getText().toString();
                ChatItem item = new ChatItem();
                item.setContent(content);
                item.setFromId(fromId);
                item.setToId(toId);
                item.setSendTime(sendTime);
                item.setFlag(false);
                mChatBinder.sendMessage(item);
                inputText.setText("");
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstVisibleItem == 0 && hasMore) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    requestForChatRecord();
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            }
        });

        mChatReceiver = new ChatReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.linxl.circle.NEW_MESSAGE");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
        mLocalBroadcastManager.registerReceiver(mChatReceiver, intentFilter);
    }

    private void requestForChatRecord() {
        RequestBody requestBody = new FormBody.Builder()
                .add("currentId", currentId)
                .add("fromId", String.valueOf(fromId))
                .add("toId", String.valueOf(toId))
                .build();

        String address = getString(R.string.server_ip) + "chatRecordServlet";
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(ChatActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    if (responseData.equals("NoMoreData")){
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                hasMore = false;
                            }
                        });

                    }else {
                        Gson gson = new Gson();
                        List<ChatItem> items = gson.fromJson(responseData,
                                new TypeToken<List<ChatItem>>(){}.getType());
                        ChatItem chatItem = items.get(items.size() - 1);
                        currentId = chatItem.getId();

                        Log.d("———ChatActivity———", "id & items" + currentId + items);

                        for (ChatItem item : items) {
                            mChatItems.add(0, item);
                        }
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mRecyclerView.scrollToPosition(19);
                                mChatAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(mConnection);
        mLocalBroadcastManager.unregisterReceiver(mChatReceiver);
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

    class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            ChatItem item = (ChatItem) intent.getSerializableExtra("new_msg");
            if (item.getFromId().equals(fromId) || item.getFromId().equals(toId)) {
                mChatItems.add(item);
                mRecyclerView.scrollToPosition(mChatItems.size()-1);
                mChatAdapter.notifyDataSetChanged();
                //item.setFlag(true);
                //item.save();
            }

        }
    }
}
