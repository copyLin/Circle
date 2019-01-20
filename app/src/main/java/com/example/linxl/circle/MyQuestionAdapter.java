package com.example.linxl.circle;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.QuestionItem;
import com.example.linxl.circle.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2019/1/17.
 */

public class MyQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

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
        ImageButton moreButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            sendTime = (TextView) view.findViewById(R.id.send_time);
            questionContent = (TextView) view.findViewById(R.id.question_content);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            moreButton = (ImageButton) view.findViewById(R.id.button_more);
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

    public MyQuestionAdapter(List<QuestionItem> questionItems){
        mQuestionItems = questionItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_question, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);
            int position = holder.getAdapterPosition();
            final QuestionItem item = mQuestionItems.get(position);
            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    break;
                                case R.id.hide:
                                    break;
                                case R.id.like:
                                    break;
                                case R.id.report:
                                    break;
                                default:

                            }
                            return true;
                        }
                    });
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {

                        }
                    });

                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);

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

            Glide.with(mContext).load(R.string.server_ip + "image/user_img/" + questionItem.getUserImg()).into(((NormalViewHolder) holder).mCircleImageView);

            if (questionItem.getQuestionImgs() != null){
                List<String> imgPaths = new ArrayList<>();
                for (String imgPath : questionItem.getQuestionImgs()){
                    imgPaths.add(R.string.server_ip + "image/" + questionItem.getUserId() + "/" + imgPath);
                }
                GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
                ImageAdapter adapter = new ImageAdapter(imgPaths);
                ((NormalViewHolder) holder).mRecyclerView.setLayoutManager(layoutManager);
                ((NormalViewHolder) holder).mRecyclerView.setAdapter(adapter);

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
