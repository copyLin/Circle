package com.example.linxl.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.ImageLoader;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewQuestionActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;
    private ProgressBar mProgressBar;
    private EditText content;
    private ImageButton addImgButton;
    private RecyclerView mRecyclerView;
    private List<String> imgPaths = new ArrayList<>();
    private ImageAdapter mImageAdapter;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        content = (EditText) findViewById(R.id.question_content);
        addImgButton = (ImageButton) findViewById(R.id.add_image);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mImageLoader = new ImageLoader(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mImageAdapter = new ImageAdapter(imgPaths);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mImageAdapter);

        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("—————— NewQuestion ————", "click the button");
                final String[] items = {"拍摄", "从相册选择"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(NewQuestionActivity.this);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                startActivityForResult(mImageLoader.takePhoto(), TAKE_PHOTO);
                                break;
                            case 1:
                                startActivityForResult(mImageLoader.choosePhoto(), CHOOSE_PHOTO);
                                break;
                            default:
                        }
                    }
                });
                dialog.show();

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                mProgressBar.setVisibility(View.VISIBLE);

                String userId = (String) SPUtil.getParam(this, SPUtil.USER_ID, "");
                String questionContent = content.getText().toString();
                String sendTime = TimeCapture.getChinaTime();

                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmssSS");
                String imgName = null;
                try {
                    imgName = formatter2.format(formatter1.parse(sendTime));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
                multipartBody.addFormDataPart("userId", userId);
                multipartBody.addFormDataPart("content", questionContent);
                multipartBody.addFormDataPart("sendTime", sendTime);
                if (imgPaths != null) {
                    int num = 0;
                    for (String imagePath : imgPaths){
                        num ++;
                        File image = new File(imagePath);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), image);
                        multipartBody.addFormDataPart("image_" + num, "IMG_" + imgName + "_" + num + ".jpg", requestBody);
                    }
                }

                RequestBody requestBody = multipartBody.build();
                String address = getString(R.string.server_ip) + "newQuestionServlet";

                mProgressBar.setProgress(90);

                HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        NewQuestionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(100);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(NewQuestionActivity.this, "信息发送失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            NewQuestionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(100);
                                    Toast.makeText(NewQuestionActivity.this, "信息发送成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }

                    }
                });
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    imgPaths.add(mImageLoader.handleTakenPhoto());
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        imgPaths.add(mImageLoader.handleImageOnKitKat(data));
                    }else {
                        imgPaths.add(mImageLoader.handleImageBeforeKitKat(data));
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(imgPaths.size()>=9){
            addImgButton.setBackgroundResource(R.drawable.ic_add_img_unable);
            addImgButton.setEnabled(false);
        }
        mRecyclerView.setAdapter(mImageAdapter);
    }
}
