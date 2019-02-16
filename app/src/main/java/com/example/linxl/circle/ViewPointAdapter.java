package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ViewPointItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/27.
 */

public class ViewPointAdapter extends RecyclerView.Adapter<ViewPointAdapter.ViewHolder> {

    private Context mContext;
    private List<ViewPointItem> mViewPointItems;

    class ViewHolder extends RecyclerView.ViewHolder{

        View commentView;
        CircleImageView mCircleImageView;
        TextView userName;
        TextView content;
        TextView sendTime;

        public ViewHolder(View view){
            super(view);
            commentView = view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            content = (TextView) view.findViewById(R.id.content);
            sendTime = (TextView) view.findViewById(R.id.send_time);
        }
    }

    public ViewPointAdapter(List<ViewPointItem> viewPointItems){
        mViewPointItems = viewPointItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpoint, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ViewPointItem item = mViewPointItems.get(position);
                Intent intent = new Intent(mContext, UserCardActivity.class);
                intent.putExtra("userId", item.getUserId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        ViewPointItem item = mViewPointItems.get(position);
        holder.userName.setText(item.getUserName());
        holder.content.setText(item.getContent());
        holder.sendTime.setText(item.getSendTime());
        Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "user_img/" + item.getUserImg()).into(holder.mCircleImageView);
    }

    @Override
    public int getItemCount(){
        return mViewPointItems.size();
    }
}
