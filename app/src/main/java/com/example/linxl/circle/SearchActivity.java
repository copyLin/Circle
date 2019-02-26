package com.example.linxl.circle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.linxl.circle.gson.DeliveryItem;
import com.example.linxl.circle.gson.IdleItem;
import com.example.linxl.circle.gson.LostItem;
import com.example.linxl.circle.gson.QuestionItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private EditText mEditText;
    private ImageButton mImageButton;
    private RecyclerView mRecyclerView;
    private String selectedLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_search);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mEditText = (EditText) findViewById(R.id.search_text);
        mImageButton = (ImageButton) findViewById(R.id.button_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLab = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchResult(selectedLab, mEditText.getText().toString());
            }
        });
    }

    private void getSearchResult(final String label, String text){
        String address = this.getString(R.string.server_ip) + "searchServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("label", label)
                .add("text", text)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(SearchActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    if (responseData.equals("NoData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SearchActivity.this, "没有相关内容", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Gson gson = new Gson();
                        switch (label){
                            case "校园话题":
                                List<QuestionItem> questionItems = gson.fromJson(responseData,
                                        new TypeToken<List<QuestionItem>>(){}.getType());
                                final LinearLayoutManager questionManager = new LinearLayoutManager(SearchActivity.this);
                                final QuestionAdapter questionAdapter = new QuestionAdapter(questionItems);
                                questionAdapter.changeState(1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setLayoutManager(questionManager);
                                        mRecyclerView.setAdapter(questionAdapter);
                                    }
                                });
                                break;
                            case "寻物启事":
                                List<LostItem> lostItems = gson.fromJson(responseData,
                                        new TypeToken<List<LostItem>>(){}.getType());
                                final GridLayoutManager lostManager = new GridLayoutManager(SearchActivity.this, 2);
                                final LostAdapter lostAdapter = new LostAdapter(lostItems);
                                lostAdapter.changeState(1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setLayoutManager(lostManager);
                                        mRecyclerView.setAdapter(lostAdapter);
                                    }
                                });
                                break;
                            case "闲置物品":
                                List<IdleItem> idleItems = gson.fromJson(responseData,
                                        new TypeToken<List<IdleItem>>(){}.getType());
                                final LinearLayoutManager idleManager = new LinearLayoutManager(SearchActivity.this);
                                final IdleAdapter idleAdapter = new IdleAdapter(idleItems);
                                idleAdapter.changeState(1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setLayoutManager(idleManager);
                                        mRecyclerView.setAdapter(idleAdapter);
                                    }
                                });
                                break;
                            case "校园跑腿":
                                List<DeliveryItem> deliveryItems = gson.fromJson(responseData,
                                        new TypeToken<List<DeliveryItem>>(){}.getType());
                                final GridLayoutManager deliveryManager = new GridLayoutManager(SearchActivity.this, 3);
                                final DeliveryAdapter deliveryAdapter = new DeliveryAdapter(deliveryItems);
                                deliveryAdapter.changeState(1);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setLayoutManager(deliveryManager);
                                        mRecyclerView.setAdapter(deliveryAdapter);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }
}
