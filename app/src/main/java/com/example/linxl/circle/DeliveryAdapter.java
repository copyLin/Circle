package com.example.linxl.circle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.linxl.circle.gson.DeliveryItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/11.
 */

public class DeliveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<DeliveryItem> mDeliveryItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        CircleImageView mImageView;
        TextView sendTime;
        TextView deliveryContent;
        TextView price;
        ImageButton connectButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mImageView = (CircleImageView) view.findViewById(R.id.user_image);
            sendTime = (TextView) view.findViewById(R.id.delivery_time);
            deliveryContent = (TextView) view.findViewById(R.id.delivery_content);
            price = (TextView) view.findViewById(R.id.delivery_price);
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

    public DeliveryAdapter(List<DeliveryItem> deliveryItems){
        mDeliveryItems = deliveryItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_delivery, parent, false);
            NormalViewHolder holder = new NormalViewHolder(view);
            holder.connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            DeliveryItem deliveryItem = mDeliveryItems.get(position);
            ((NormalViewHolder) holder).sendTime.setText(deliveryItem.getSendTime());
            ((NormalViewHolder) holder).deliveryContent.setText(deliveryItem.getContent());
            ((NormalViewHolder) holder).price.setText(deliveryItem.getPrice());

            if (deliveryItem.getUserId().equals(userId)) {
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
        if (mDeliveryItems != null){
            return mDeliveryItems.size() + 1;
        }
        return mDeliveryItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }
}
