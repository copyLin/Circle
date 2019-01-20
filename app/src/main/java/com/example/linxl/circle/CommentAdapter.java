package com.example.linxl.circle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.CommentItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/27.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentItem> mCommentItems;

    class ViewHolder extends RecyclerView.ViewHolder{

        View commentView;
        CircleImageView mCircleImageView;
        TextView userName;
        TextView commentContent;
        TextView commentTime;

        public ViewHolder(View view){
            super(view);
            commentView = view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            commentContent = (TextView) view.findViewById(R.id.comment_content);
            commentTime = (TextView) view.findViewById(R.id.comment_time);
        }
    }

    public CommentAdapter(List<CommentItem> commentItems){
        mCommentItems = commentItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        CommentItem item = mCommentItems.get(position);
        holder.userName.setText(item.getUserName());
        holder.commentContent.setText(item.getCommentContent());
        holder.commentTime.setText(item.getCommentTime());
        Glide.with(mContext).load(R.string.server_ip + "user_img/" + item.getUserImg()).into(holder.mCircleImageView);
    }

    @Override
    public int getItemCount(){
        return mCommentItems.size();
    }
}
