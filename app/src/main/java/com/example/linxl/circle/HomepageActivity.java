package com.example.linxl.circle;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomepageActivity extends AppCompatActivity {

    private CircleImageView mCircleImageView;
    private TextView userId;
    private TextView userName;
    private TextView userDepartment;
    private TextView userMajor;
    private TextView userWords;
    private Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        mCircleImageView = (CircleImageView) findViewById(R.id.user_image);
        userId = (TextView) findViewById(R.id.user_id);
        userName = (TextView) findViewById(R.id.user_name);
        userDepartment = (TextView) findViewById(R.id.user_department);
        userMajor = (TextView) findViewById(R.id.user_major);
        userWords = (TextView) findViewById(R.id.user_words);
        changePassword = (Button) findViewById(R.id.change_password);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText oldPassword = new EditText(HomepageActivity.this);
                final EditText newPassword = new EditText(HomepageActivity.this);
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                builder.setView(oldPassword);
                builder.setView(newPassword);
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = R.string.server_ip + "changePasswordServlet";
                        RequestBody requestBody = new FormBody.Builder()
                                .add("oldPassword", oldPassword.getText().toString())
                                .add("newPassword", newPassword.getText().toString())
                                .build();
                        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(HomepageActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(HomepageActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
