package com.example.linxl.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.QuestionItem;
import com.example.linxl.circle.gson.ViewPointItem;
import com.example.linxl.circle.utils.ActivityCollector;
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

public class QuestionDetailActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private CircleImageView mCircleImageView;
    private TextView userName;
    private TextView sendTime;
    private TextView content;
    private ImageButton commentButton;
    private ImageButton connectButton;
    private ImageButton sendButton;
    private EditText inputText;
    private RecyclerView images;
    private RecyclerView viewPoint;
    private TextView nullContent;
    private TextView nullViewPoint;
    private View itemDetailLayout;

    private ImageHorizontalViewAdapter mImageAdapter;
    private ViewPointAdapter mViewPointAdapter;

    private QuestionItem mQuestionItem;
    private List<String> imgPaths;
    private List<ViewPointItem> mViewPointItems;

    private boolean collectionState = false;
    private boolean contentState = true;
    private String myId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");
    private String userId;
    private String keyId;
    private String label;
    private String reportReason = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_question_detail);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mCircleImageView = (CircleImageView) findViewById(R.id.user_image);
        userName = (TextView) findViewById(R.id.user_name);
        sendTime = (TextView) findViewById(R.id.send_time);
        content = (TextView) findViewById(R.id.question_content);
        commentButton = (ImageButton) findViewById(R.id.button_comment);
        connectButton = (ImageButton) findViewById(R.id.button_connect);
        sendButton = (ImageButton) findViewById(R.id.send);
        inputText = (EditText) findViewById(R.id.input_text);
        images = (RecyclerView) findViewById(R.id.question_images);
        viewPoint = (RecyclerView) findViewById(R.id.view_point);
        nullContent = (TextView) findViewById(R.id.hint_null_content);
        nullViewPoint = (TextView) findViewById(R.id.hint_null_viewpoint);
        itemDetailLayout = (View) findViewById(R.id.item_detail_layout);
        imgPaths = new ArrayList<>();
        mViewPointItems = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_popup));

        Intent intent = getIntent();
        keyId = intent.getStringExtra("keyId");
        label = intent.getStringExtra("label");
        userId = intent.getStringExtra("userId");

        getItemDetail(keyId, label);
        getItemViewPoint(keyId, label);
        getCollectionState(keyId, label);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(inputText);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionDetailActivity.this, ChatActivity.class);
                intent.putExtra("fromId", (String) SPUtil.getParam(QuestionDetailActivity.this, SPUtil.USER_ID, ""));
                intent.putExtra("toId", mQuestionItem.getUserId());
                intent.putExtra("contactName", mQuestionItem.getUserName());
                intent.putExtra("contactImg", mQuestionItem.getUserImg());
                startActivity(intent);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                String content = inputText.getText().toString();
                if (content.equals("")){
                    Toast.makeText(QuestionDetailActivity.this, "请输入评论信息", Toast.LENGTH_SHORT).show();
                }else {
                    String address = getString(R.string.server_ip) + "newViewPointServlet";
                    RequestBody requestBody = new FormBody.Builder()
                            .add("keyId", mQuestionItem.getQuestionId())
                            .add("label", "Question")
                            .add("toId", mQuestionItem.getUserId())
                            .add("content", content)
                            .add("userId", (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, ""))
                            .add("sendTime", TimeCapture.getChinaTime())
                            .build();

                    mProgressBar.setProgress(60);

                    HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(100);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(QuestionDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(100);
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        inputText.setText("");
                                        hideKeyboard();
                                        Toast.makeText(QuestionDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                                        getItemViewPoint(keyId, label);
                                    }
                                });
                            }
                        }
                    });
                }


            }
        });

        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionDetailActivity.this, UserCardActivity.class);
                intent.putExtra("userId", mQuestionItem.getUserId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_popup, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (contentState){
            if (myId.equals(userId)){
                menu.findItem(R.id.delete).setVisible(true);
                if (mQuestionItem != null && mQuestionItem.isFlag()){
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
            if (collectionState){
                menu.findItem(R.id.like).setTitle("已收藏").setEnabled(false);
            }else {
                menu.findItem(R.id.like).setTitle("收藏").setEnabled(true);
            }
        }else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.hide).setVisible(false);
            menu.findItem(R.id.open).setVisible(false);
            menu.findItem(R.id.like).setVisible(false);
            menu.findItem(R.id.report).setVisible(false);
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
                        deleteMyQuestion(keyId);
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
                        hideMyQuestion(keyId);
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
                        openMyQuestion(keyId);
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
                View view = LayoutInflater.from(QuestionDetailActivity.this).inflate(R.layout.dialog_new_collection, null);
                final EditText name = view.findViewById(R.id.collection_name);
                AlertDialog.Builder dialog4 = new android.support.v7.app.AlertDialog.Builder(this);
                dialog4.setView(view);
                dialog4.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCollection(name.getText().toString(), myId, mQuestionItem.getQuestionId());
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
            case R.id.report:

                View reportView = LayoutInflater.from(QuestionDetailActivity.this).inflate(R.layout.dialog_report, null);
                RadioGroup radioGroup = reportView.findViewById(R.id.radio_group);
                final RadioButton item1 = reportView.findViewById(R.id.item_1);
                final RadioButton item2 = reportView.findViewById(R.id.item_2);
                final RadioButton item3 = reportView.findViewById(R.id.item_3);
                final RadioButton item4 = reportView.findViewById(R.id.item_4);
                final RadioButton item5 = reportView.findViewById(R.id.item_5);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == item1.getId()){
                            reportReason = getString(R.string.radio_item_1);
                        }else if (checkedId == item2.getId()){
                            reportReason = getString(R.string.radio_item_2);
                        }else if (checkedId == item3.getId()){
                            reportReason = getString(R.string.radio_item_3);
                        }else if (checkedId == item4.getId()){
                            reportReason = getString(R.string.radio_item_4);
                        }else if (checkedId == item5.getId()){
                            reportReason = getString(R.string.radio_item_5);
                        }
                    }
                });

                AlertDialog.Builder dialog5 = new android.support.v7.app.AlertDialog.Builder(this);
                dialog5.setView(reportView);
                dialog5.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("QuestionDetail", "reason" + reportReason);

                        sendReport(mQuestionItem.getQuestionId(), reportReason, myId);
                    }
                });
                dialog5.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog5.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void getItemDetail(String keyId, String label){
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
                        Toast.makeText(QuestionDetailActivity.this, "内容加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    if (responseData.equals("NoData")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nullContent.setVisibility(View.VISIBLE);
                                itemDetailLayout.setVisibility(View.INVISIBLE);
                                contentState = false;
                                inputText.setFocusable(false);
                                sendButton.setEnabled(false);
                                invalidateOptionsMenu();
                            }
                        });
                    }else {
                        Gson gson = new Gson();
                        mQuestionItem = gson.fromJson(responseData, QuestionItem.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nullContent.setVisibility(View.INVISIBLE);
                                itemDetailLayout.setVisibility(View.VISIBLE);
                                userName.setText(mQuestionItem.getUserName());
                                sendTime.setText(mQuestionItem.getSendTime());
                                content.setText(mQuestionItem.getContent());
                                Glide.with(QuestionDetailActivity.this).load(getString(R.string.server_ip) + "image/user_img/" + mQuestionItem.getUserImg()).into(mCircleImageView);

                                if (!mQuestionItem.getQuestionImgs().isEmpty()){
                                    for (String imgPath : mQuestionItem.getQuestionImgs()){
                                        imgPaths.add(getString(R.string.server_ip) + "image/" + mQuestionItem.getUserId() + "/" + imgPath);
                                    }
                                }

                                if (mQuestionItem.getUserId().equals(myId)){
                                    connectButton.setBackgroundResource(R.drawable.ic_connect_unable);
                                    connectButton.setEnabled(false);
                                }else {
                                    connectButton.setBackgroundResource(R.drawable.ic_connect);
                                    connectButton.setEnabled(true);
                                }

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QuestionDetailActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                mImageAdapter = new ImageHorizontalViewAdapter(imgPaths);
                                images.setLayoutManager(linearLayoutManager);
                                images.setAdapter(mImageAdapter);

                                invalidateOptionsMenu();
                            }
                        });
                    }

                }
            }
        });
    }

    private void getItemViewPoint(String keyId, String label){
        String address = getString(R.string.server_ip) + "viewPointServlet";
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
                        Toast.makeText(QuestionDetailActivity.this, "评论加载失败", Toast.LENGTH_SHORT).show();
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
                                nullViewPoint.setVisibility(View.VISIBLE);

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
                                nullViewPoint.setVisibility(View.INVISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(QuestionDetailActivity.this);
                                mViewPointAdapter = new ViewPointAdapter(mViewPointItems);
                                viewPoint.setLayoutManager(linearLayoutManager);
                                viewPoint.setAdapter(mViewPointAdapter);

                            }
                        });
                    }
                }
            }
        });

    }

    private void getCollectionState(String keyId, String label){
        String address = this.getString(R.string.server_ip) + "collectionStateServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", myId)
                .add("keyId", keyId)
                .add("label", label)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    if (responseData.equals("StateTrue")){
                        collectionState = true;
                    }else if (responseData.equals("StateFalse")){
                        collectionState = false;
                    }else {
                        collectionState = false;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidateOptionsMenu();
                        }
                    });

                }
            }
        });
    }

    private void deleteMyQuestion(String keyId) {
        String address = getString(R.string.server_ip) + "deleteQuestionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", keyId)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
                            finish();
                            Toast.makeText(QuestionDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void hideMyQuestion(String keyId) {
        String address = getString(R.string.server_ip) + "updateQuestionFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", keyId)
                .add("flag", "true")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mQuestionItem.setFlag(true);
                            invalidateOptionsMenu();
                        }
                    });
                }
            }
        });
    }

    private void openMyQuestion(String keyId) {
        String address = getString(R.string.server_ip) + "updateQuestionFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", keyId)
                .add("flag", "false")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mQuestionItem.setFlag(false);
                            invalidateOptionsMenu();
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
                .add("label", "Question")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QuestionDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
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
                            collectionState = true;
                            invalidateOptionsMenu();
                        }
                    });
                }
            }
        });
    }

    private void sendReport(String keyId, String reason, String userId){
        if (reason.equals("")){

        }else {
            String address = getString(R.string.server_ip) + "newReportServlet";
            RequestBody requestBody = new FormBody.Builder()
                    .add("keyId", keyId)
                    .add("label", "Question")
                    .add("reason", reason)
                    .add("userId", userId)
                    .build();
            HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(QuestionDetailActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(QuestionDetailActivity.this, responseData, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    }

    private void showKeyboard(EditText editText){
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getWindow().peekDecorView();
        if (null != view){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }
}
