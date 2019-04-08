 package com.example.linxl.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.ImageLoader;
import com.example.linxl.circle.utils.SPUtil;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

 public class UserImageActivity extends AppCompatActivity {

     public static final int TAKE_PHOTO = 0;
     public static final int CHOOSE_PHOTO = 1;

     private boolean doneMenuState = false;

     private ImageView mImageView;
     private ImageLoader mImageLoader;

     private String imgPath;
     private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_user_image);
        mImageView = (ImageView) findViewById(R.id.user_image);
        mImageLoader = new ImageLoader(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Glide.with(UserImageActivity.this).load(getString(R.string.server_ip) + "image/user_img/" + SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_IMG, "")).into(mImageView);
    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.menu_user_image, menu);
         return true;
     }

     @Override
     public boolean onPrepareOptionsMenu(Menu menu){
         if (doneMenuState){
             menu.findItem(R.id.done).setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
         }else {
             menu.findItem(R.id.done).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
         }
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 finish();
                 break;
             case R.id.change:
                 final String[] items = {"拍摄", "从相册选择"};
                 AlertDialog.Builder dialog = new AlertDialog.Builder(UserImageActivity.this);
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
                 break;
             case R.id.done:
                 File image = new File(imgPath);
                 MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
                 RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), image);
                 multipartBody.addFormDataPart( userId, userId + ".jpg", imageBody);
                 multipartBody.addFormDataPart("userId", userId);
                 RequestBody requestBody = multipartBody.build();
                 String address = getString(R.string.server_ip) + "uploadUserImage";
                 HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
                     @Override
                     public void onFailure(Call call, IOException e) {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(UserImageActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
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
                                     if (responseData.equals("Success")){
                                         doneMenuState = false;
                                         invalidateOptionsMenu();
                                         Toast.makeText(UserImageActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                     }else {
                                         Toast.makeText(UserImageActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });
                         }
                     }
                 });
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
                     Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
                     Crop.of(mImageLoader.getImageUri(), destination).asSquare().start(this);
                 }
                 break;
             case CHOOSE_PHOTO:
                 if (resultCode == RESULT_OK){
                     Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
                     Crop.of(data.getData(), destination).asSquare().start(this);
                 }
                 break;
             case Crop.REQUEST_CROP:
                 handleCrop(resultCode, data);
             default:
                 break;
         }
     }

     private void handleCrop(int resultCode, Intent data) {
         switch (resultCode){
             case RESULT_OK:
                 Log.d("————ImageCrop————", "" + Crop.getOutput(data));
                 imgPath = Crop.getOutput(data).getPath();
                 Glide.with(UserImageActivity.this).load(imgPath).into(mImageView);
                 SPUtil.setParam(UserImageActivity.this, SPUtil.USER_IMG, userId + ".jpg");
                 doneMenuState = true;
                 invalidateOptionsMenu();
                 break;
             case Crop.RESULT_ERROR:
                 Log.d("————ImageCrop————", "crop fail");
                 break;
             default:
                 break;
         }
     }

     @Override
     protected void onDestroy(){
         super.onDestroy();
         ActivityCollector.removeAvtivity(this);
     }
 }
