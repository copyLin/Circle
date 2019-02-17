package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.IdleItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.List;

/**
 * Created by Linxl on 2018/11/11.
 */

public class IdleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<IdleItem> mIdleItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        ImageView mImageView;
        TextView idleName;
        TextView idleContent;
        TextView idlePrice;
        ImageButton commentButton;
        ImageButton connectButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mImageView = (ImageView) view.findViewById(R.id.idle_image);
            idleName = (TextView) view.findViewById(R.id.idle_name);
            idleContent = (TextView) view.findViewById(R.id.idle_content);
            idlePrice = (TextView) view.findViewById(R.id.idle_price);
            commentButton = (ImageButton) view.findViewById(R.id.button_comment);
            connectButton = (ImageButton) view.findViewById(R.id.button_connect);
        }

    }

    class FooterViewHolder extends RecyclerView.ViewHolder{

        ProgressBar mProgressBar;
        TextView footerState;

        public FooterViewHolder(View view){
            super(view);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            footerState = (TextView) view.findViewById(R.id.footer_state);
        }
    }

    public IdleAdapter(List<IdleItem> idleItems){
        mIdleItems = idleItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_idle, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    IdleItem item = mIdleItems.get(position);
                    Intent intent = new Intent(mContext, IdleDetailActivity.class);
                    intent.putExtra("keyId", item.getIdleId());
                    intent.putExtra("label", "Idle");
                    intent.putExtra("userId", item.getUserId());
                    mContext.startActivity(intent);
                }
            });
            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    IdleItem item = mIdleItems.get(position);
                    Intent intent = new Intent(mContext, IdleDetailActivity.class);
                    intent.putExtra("keyId", item.getIdleId());
                    intent.putExtra("label", "Idle");
                    intent.putExtra("userId", item.getUserId());
                    mContext.startActivity(intent);
                }
            });
            holder.connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    IdleItem item = mIdleItems.get(position);
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("fromId", (String) SPUtil.getParam(mContext, SPUtil.USER_ID, ""));
                    intent.putExtra("toId", item.getUserId());
                    intent.putExtra("contactImg", item.getUserImg());
                    mContext.startActivity(intent);
                }
            });
            return holder;
        } else if (viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(mContext).inflate(R.layout.footer, parent, false);
            FooterViewHolder holder = new FooterViewHolder(view);
            return holder;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if (holder instanceof NormalViewHolder){
            IdleItem idleItem = mIdleItems.get(position);
            ((NormalViewHolder) holder).idleName.setText(idleItem.getIdleName());
            ((NormalViewHolder) holder).idleContent.setText(idleItem.getContent());
            ((NormalViewHolder) holder).idlePrice.setText("¥ " + idleItem.getPrice());
            if (!idleItem.getIdleImgs().isEmpty()) {
                Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/" + idleItem.getUserId() + "/" + idleItem.getIdleImgs().get(0)).into(((NormalViewHolder) holder).mImageView);
            }
            if (idleItem.getUserId().equals(userId)) {
                ((NormalViewHolder) holder).connectButton.setBackgroundResource(R.drawable.ic_connect_unable);
                ((NormalViewHolder) holder).connectButton.setEnabled(false);
            }else {
                ((NormalViewHolder) holder).connectButton.setBackgroundResource(R.drawable.ic_connect);
                ((NormalViewHolder) holder).connectButton.setEnabled(true);
            }
        }else if (holder instanceof FooterViewHolder){
            if (position == 0) {
                ((FooterViewHolder)holder).mProgressBar.setVisibility(View.GONE);
                ((FooterViewHolder)holder).footerState.setText("");
            }
            switch (footer_state) {
                case LOADING_MORE:
                    ((FooterViewHolder)holder).mProgressBar.setVisibility(View.VISIBLE);
                    ((FooterViewHolder)holder).footerState.setText(" 正在加载");
                    break;
                case NO_MORE:
                    ((FooterViewHolder)holder).mProgressBar.setVisibility(View.GONE);
                    ((FooterViewHolder)holder).footerState.setText("—— 我是有底线的 ——");
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position){
        if (position == getItemCount() - 1 ){
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;

    }

    @Override
    public int getItemCount(){
        if (mIdleItems != null){
            return mIdleItems.size() + 1;
        }
        return mIdleItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }

}
