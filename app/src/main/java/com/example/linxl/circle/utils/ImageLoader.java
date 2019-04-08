package com.example.linxl.circle.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;


import com.example.linxl.circle.MyApplication;
import com.example.linxl.circle.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Linxl on 2018/11/11.
 */

public class ImageLoader {

    private Context mContext;

    private Intent mIntent;

    private Uri imageUri;

    public ImageLoader(Context context){
        mContext = context;
    }

    public  Intent choosePhoto() {
        if (ContextCompat.checkSelfPermission(MyApplication.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            mIntent = new Intent();
        }else {
            mIntent = new Intent("android.intent.action.GET_CONTENT");
            mIntent.setType("image/*");
        }
        return mIntent;
    }

    public Intent takePhoto() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String imgName = "IMG_" + formatter.format(curDate) + ".jpg";
        File outputImage = new File(mContext.getExternalCacheDir(), imgName);
        try{
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(mContext,
                    "com.example.linxl.circle.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        mIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return mIntent;
    }

    public String handleTakenPhoto(){
        String imagePath = imageUri.getPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap origin = BitmapFactory.decodeFile(imagePath);
        int height = origin.getHeight();
        int width = origin.getWidth();

        if (height > 4096 || width > 4096){
            if (height > 8192 || width > 8192){
                options.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                try{
                    FileOutputStream outputStream = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }else {
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                try{
                    FileOutputStream outputStream = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

        }
        return imagePath;
    }

    @TargetApi(19)
    public String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(mContext, uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse(
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap origin = BitmapFactory.decodeFile(imagePath);
        int height = origin.getHeight();
        int width = origin.getWidth();

        if (height > 4096 || width > 4096){
            if (height > 8192 || width > 8192){
                options.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                try{
                    FileOutputStream outputStream = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }else {
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                try{
                    FileOutputStream outputStream = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }

        }

        return imagePath;
    }

    public String handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        if (outHeight > 2500 || outWidth > 2500){
            try{
                FileOutputStream outputStream = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }else {

        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = mContext.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public Uri getImageUri(){
        return imageUri;
    }
}
