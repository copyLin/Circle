package com.example.linxl.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxl on 2018/11/11.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mStrings;

    static final int DELETE_IMAGE = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case DELETE_IMAGE:
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageButton deleteButton;

        public ViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            deleteButton = (ImageButton) view.findViewById(R.id.delete_button);
        }
    }

    public ImageAdapter(List<String> imgPaths){
        mStrings = imgPaths;

    }

    public List<String> getStrings() {
        return mStrings;
    }

    public void setStrings(List<String> imgPaths) {
        mStrings = imgPaths;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(v.getContext(), ImageDetailActivity.class);
                intent.putExtra("num", mStrings.size());
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("imgPaths", (ArrayList<String>)mStrings);
                mContext.startActivity(intent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final String imagePath = mStrings.get(position);
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(mContext);
                dialog1.setMessage("是否删除该图片");
                dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStrings.remove(imagePath);
                        Message message = new Message();
                        message.what = DELETE_IMAGE;
                        mHandler.sendMessage(message);
                    }
                });
                dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog1.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String imgPath = mStrings.get(position);
        Glide.with(mContext).load(imgPath).into(holder.image);
    }

    @Override
    public int getItemCount(){
        return mStrings.size();
    }
}
