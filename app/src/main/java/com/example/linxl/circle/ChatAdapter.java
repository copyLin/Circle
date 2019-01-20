package com.example.linxl.circle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/26.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int LEFT_ITEM = 0;
    static final int RIGHT_ITEM = 1;

    private Context mContext;
    private List<ChatItem> mChatItems;
    private String contactImg;
    private String userId = (String) SPUtil.getParam(mContext, SPUtil.USER_ID, "");
    private String userImg = (String) SPUtil.getParam(mContext, SPUtil.USER_IMG, "");

    class LeftViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView content;

        public LeftViewHolder(View view){
            super(view);
            userImg = (CircleImageView) view.findViewById(R.id.user_img_left);
            content = (TextView) view.findViewById(R.id.left_msg);
        }
    }

    class RightViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImg;
        TextView content;

        public RightViewHolder(View view){
            super(view);
            userImg = (CircleImageView) view.findViewById(R.id.user_img_right);
            content = (TextView) view.findViewById(R.id.right_msg);
        }
    }

    public ChatAdapter(List<ChatItem> chatItems, String contactImg){
        mChatItems = chatItems;
        this.contactImg = contactImg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == LEFT_ITEM){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            LeftViewHolder holder = new LeftViewHolder(view);
            return holder;
        }else if (viewType == RIGHT_ITEM){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            RightViewHolder holder = new RightViewHolder(view);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatItem item = mChatItems.get(position);
        if (holder instanceof LeftViewHolder) {
            Glide.with(mContext).load(R.string.server_ip + "user_img/" + contactImg).into(((LeftViewHolder) holder).userImg);
            ((LeftViewHolder) holder).content.setText(item.getContent());
        }else if (holder instanceof RightViewHolder) {
            Glide.with(mContext).load(R.string.server_ip + "user_img/" + userImg).into(((RightViewHolder) holder).userImg);
            ((RightViewHolder) holder).content.setText(item.getContent());
        }
    }

    @Override
    public int getItemViewType(int position){
        ChatItem item = mChatItems.get(position);
        if (item.getFromId().equals(userId)) {
            return RIGHT_ITEM;
        }
        return LEFT_ITEM;
    }

    @Override
    public int getItemCount(){
        return mChatItems.size();
    }
}
