package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.QuestionItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/11.
 */

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<QuestionItem> mQuestionItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        CircleImageView mCircleImageView;
        TextView userName;
        TextView sendTime;
        TextView questionContent;
        RecyclerView mRecyclerView;
        ImageButton commentButton;
        ImageButton connectButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            sendTime = (TextView) view.findViewById(R.id.send_time);
            questionContent = (TextView) view.findViewById(R.id.question_content);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

    public QuestionAdapter(List<QuestionItem> questionItems){
        mQuestionItems = questionItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);
            int position = holder.getAdapterPosition();
            final QuestionItem item = mQuestionItems.get(position);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("keyId", item.getQuestionId());
                    intent.putExtra("label", "Question");
                    intent.putExtra("userId", item.getUserId());
                    mContext.startActivity(intent);
                }
            });
            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("keyId", item.getQuestionId());
                    intent.putExtra("label", "Question");
                    intent.putExtra("userId", item.getUserId());
                    mContext.startActivity(intent);
                }
            });
            holder.connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("fromId", (String) SPUtil.getParam(mContext, SPUtil.USER_ID, ""));
                    intent.putExtra("toId", item.getUserId());
                    intent.putExtra("contactImg", item.getUserImg());
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
            QuestionItem questionItem = mQuestionItems.get(position);
            ((NormalViewHolder) holder).userName.setText(questionItem.getUserName());
            ((NormalViewHolder) holder).sendTime.setText(questionItem.getSendTime());
            ((NormalViewHolder) holder).questionContent.setText(questionItem.getContent());

            Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/user_img/" + questionItem.getUserImg()).into(((NormalViewHolder) holder).mCircleImageView);

            if (questionItem.getQuestionImgs() != null){
                List<String> imgPaths = new ArrayList<>();
                for (String imgPath : questionItem.getQuestionImgs()){
                    imgPaths.add(mContext.getResources().getString(R.string.server_ip) + "image/" + questionItem.getUserId() + "/" + imgPath);
                }
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                ImageAdapter adapter = new ImageAdapter(imgPaths);
                ((NormalViewHolder) holder).mRecyclerView.setLayoutManager(layoutManager);
                ((NormalViewHolder) holder).mRecyclerView.setAdapter(adapter);

                if (questionItem.getUserId().equals(userId)) {
                    ((NormalViewHolder) holder).connectButton.setBackgroundResource(R.drawable.ic_connect_unable);
                    ((NormalViewHolder) holder).connectButton.setEnabled(false);
                }else {
                    ((NormalViewHolder) holder).connectButton.setBackgroundResource(R.drawable.ic_connect);
                    ((NormalViewHolder) holder).connectButton.setEnabled(true);
                }
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
    public int getItemViewType(int positon){
        if (positon == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount(){
        if (mQuestionItems != null){
            return mQuestionItems.size() + 1;
        }
        return mQuestionItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }
}
