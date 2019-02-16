package com.example.linxl.circle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.linxl.circle.gson.ContactItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactsActivity extends AppCompatActivity {

    private List<ContactItem> mContactItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
        }

        requestForContact();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ContactAdapter adapter = new ContactAdapter(mContactItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void requestForContact() {
        String address = getString(R.string.server_ip) + "contactServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", (String) SPUtil.getParam(this, SPUtil.USER_ID, ""))
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ContactsActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                        Gson gson = new Gson();
                        mContactItems = gson.fromJson(responseData, new TypeToken<List<ContactItem>>(){}.getType());
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
