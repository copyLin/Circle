package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxl on 2018/11/11.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private Context mContext;
    private List<String> mStrings;


    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
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
