package com.example.linxl.circle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.linxl.circle.gson.ViewPointItem;
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

public class MyViewPointActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private List<ViewPointItem> items;
    private List<ViewPointItem> allItems;
    private LinearLayoutManager layoutManager;
    private MyViewPointAdapter adapter;
    private String currentId = "0";
    private boolean hasMore = true;
    private int lastVisibleItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_viewpoint);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        requestForMyViewPoint();

        allItems = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        adapter = new MyViewPointAdapter(allItems);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && hasMore) {
                    requestForMyViewPoint();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }

        });
    }

    private void requestForMyViewPoint() {
        String address = getString(R.string.server_ip) + "myViewPointServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", (String) SPUtil.getParam(this, SPUtil.USER_ID, ""))
                .add("currentId", currentId)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyViewPointActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();

                    if (responseData.equals("NoMoreData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hasMore = false;
                                adapter.changeState(1);
                            }
                        });

                    }else {
                        Gson gson = new Gson();
                        items = gson.fromJson(responseData,
                                new TypeToken<List<ViewPointItem>>(){}.getType());
                        ViewPointItem item = items.get(items.size() - 1);
                        currentId = item.getKeyId();
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
