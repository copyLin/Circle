package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ViewPointItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2019/1/20.
 */

public class UnreadViewPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<ViewPointItem> mViewPointItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        CircleImageView mCircleImageView;
        TextView tip;
        TextView sendTime;
        TextView content;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
            tip = (TextView) view.findViewById(R.id.tip);
            sendTime = (TextView) view.findViewById(R.id.send_time);
            content = (TextView) view.findViewById(R.id.content);
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

    public UnreadViewPointAdapter(List<ViewPointItem> viewPointItems){
        mViewPointItems = viewPointItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_viewpoint_with_tip, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    ViewPointItem viewPointItem = mViewPointItems.get(position);
                    Intent intent = null;
                    switch (viewPointItem.getLabel()){
                        case "Question":
                            intent = new Intent(mContext, QuestionDetailActivity.class);
                            intent.putExtra("keyId", viewPointItem.getKeyId());
                            intent.putExtra("label", viewPointItem.getLabel());
                            break;
                        case "Lost":
                            intent = new Intent(mContext, LostDetailActivity.class);
                            intent.putExtra("keyId", viewPointItem.getKeyId());
                            intent.putExtra("label", viewPointItem.getLabel());
                            break;
                        case "Idle":
                            intent = new Intent(mContext, IdleDetailActivity.class);
                            intent.putExtra("keyId", viewPointItem.getKeyId());
                            intent.putExtra("label", viewPointItem.getLabel());
                            break;
                        default:
                            break;
                    }
                    mContext.startActivity(intent);
                }
            });
            return holder;
        }else if (viewType == TYPE_FOOTER){
            View view = LayoutInflater.from(mContext).inflate(R.layout.footer, parent, false);
            FooterViewHolder holder = new FooterViewHolder(view);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if (holder instanceof NormalViewHolder){
            ViewPointItem item = mViewPointItems.get(position);
            ((NormalViewHolder) holder).tip.setText(item.getTip());
            ((NormalViewHolder) holder).sendTime.setText(item.getSendTime());
            ((NormalViewHolder) holder).content.setText(item.getContent());

            Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/user_img/" + item.getUserImg()).into(((NormalViewHolder) holder).mCircleImageView);

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
                    ((FooterViewHolder)holder).footerState.setText("—— 无更多评论 ——");
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int positon){
        if (positon == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount(){
        if (mViewPointItems != null){
            return mViewPointItems.size() + 1;
        }
        return mViewPointItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }

}
