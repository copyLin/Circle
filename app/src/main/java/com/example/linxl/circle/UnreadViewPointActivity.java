package com.example.linxl.circle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.linxl.circle.gson.ViewPointItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UnreadViewPointActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private List<ViewPointItem> items;
    private List<ViewPointItem> allItems;
    private LinearLayoutManager layoutManager;
    private UnreadViewPointAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_unread_viewpoint);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        allItems = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        adapter = new UnreadViewPointAdapter(allItems);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        String address = getString(R.string.server_ip) + "unreadViewPoint";
        RequestBody requestBody = new FormBody.Builder()
                .add("toId", (String) SPUtil.getParam(this, SPUtil.USER_ID, ""))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UnreadViewPointActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();

                    if (responseData.equals("NoData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UnreadViewPointActivity.this, "无未读消息", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        Gson gson = new Gson();
                        items = gson.fromJson(responseData,
                                new TypeToken<List<ViewPointItem>>(){}.getType());
                        allItems.addAll(items);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();

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
        ActivityCollector.removeAvtivity(this);
    }
}
