package com.example.linxl.circle;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity {

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setTitle("设置");
        }

        LinearLayout changerPassword = (LinearLayout) findViewById(R.id.change_password);
        LinearLayout logout = (LinearLayout) findViewById(R.id.logout);

        changerPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_update_password, null);
                final EditText oldPassword = view.findViewById(R.id.old_password);
                final EditText firstInput = view.findViewById(R.id.new_password);
                final EditText secondInput = view.findViewById(R.id.confirm_password);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setView(view);
                dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = getString(R.string.server_ip) + "updatePassword";
                        RequestBody requestBody = new FormBody.Builder()
                                .add("userId", userId)
                                .add("oldPassword", oldPassword.getText().toString())
                                .add("firstInput", firstInput.getText().toString())
                                .add("secondInput", secondInput.getText().toString())
                                .build();
                        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SettingActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(SettingActivity.this, "" + responseData, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog3 = new AlertDialog.Builder(SettingActivity.this);
                dialog3.setMessage("点击确定，退出当前用户");
                dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtil.removeParam(MyApplication.getContext(), SPUtil.USER_ID);
                        SPUtil.removeParam(MyApplication.getContext(), SPUtil.USER_NAME);
                        SPUtil.removeParam(MyApplication.getContext(), SPUtil.USER_IMG);
                        SPUtil.removeParam(MyApplication.getContext(), SPUtil.IS_LOGIN);
                        DataSupport.deleteAll(ChatItem.class);
                        ActivityCollector.finishAll();

                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                    }
                });
                dialog3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog3.show();
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
