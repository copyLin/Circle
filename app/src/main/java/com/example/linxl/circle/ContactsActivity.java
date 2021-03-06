package com.example.linxl.circle;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.linxl.circle.gson.ContactItem;
import com.example.linxl.circle.utils.ActivityCollector;
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

    private View unreadViewPoint;
    private View nullContact;
    private RecyclerView mRecyclerView;
    private List<ContactItem> mContactItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_contacts);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        unreadViewPoint = (View) findViewById(R.id.unread_viewpoint);
        nullContact = (View) findViewById(R.id.hint_null_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        requestForContact();

        unreadViewPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactsActivity.this, UnreadViewPointActivity.class));
            }
        });

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
                    if (responseData.equals("NoData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nullContact.setVisibility(View.VISIBLE);
                            }
                        });
                    }else {
                        Gson gson = new Gson();
                        mContactItems = gson.fromJson(responseData,
                                new TypeToken<List<ContactItem>>(){}.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nullContact.setVisibility(View.INVISIBLE);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(ContactsActivity.this);
                                ContactAdapter adapter = new ContactAdapter(mContactItems);
                                mRecyclerView.setLayoutManager(layoutManager);
                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    }

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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }
}
