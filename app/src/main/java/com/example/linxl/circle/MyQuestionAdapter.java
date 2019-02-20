package com.example.linxl.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.QuestionItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Linxl on 2019/1/17.
 */

public class MyQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;


    private int footer_state = 0;
    private Context mContext;
    private List<QuestionItem> mQuestionItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CONTROL_SUCCESS:
                    Toast.makeText(MyApplication.getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                    break;
                case CONTROL_FAIL:
                    Toast.makeText(MyApplication.getContext(), "操作失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

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
            mRecyclerView = (RecyclerView) view.findViewById(R.id.question_images);
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

            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    final QuestionItem questionItem = mQuestionItems.get(position);
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:
                                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(mContext);
                                    dialog1.setMessage("删除后将无法恢复，点击确定删除");
                                    dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteMyQuestion(questionItem.getUserId(), questionItem.getSendTime());
                                        }
                                    });
                                    dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog1.show();
                                    break;
                                case R.id.hide:
                                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(mContext);
                                    dialog2.setMessage("点击确定，将问题设为仅自己可见");
                                    dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            hideMyQuestion(questionItem.getUserId(), questionItem.getSendTime());
                                        }
                                    });
                                    dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog2.show();
                                    break;
                                case R.id.open:
                                    AlertDialog.Builder dialog3 = new AlertDialog.Builder(mContext);
                                    dialog3.setMessage("点击确定，将问题设为公开");
                                    dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            openMyQuestion(questionItem.getUserId(), questionItem.getSendTime());
                                        }
                                    });
                                    dialog3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog3.show();
                                    break;
                                case R.id.like:
                                    final EditText name = new EditText(mContext);
                                    AlertDialog.Builder dialog4 = new android.support.v7.app.AlertDialog.Builder(mContext);
                                    dialog4.setTitle("添加到收藏夹");
                                    dialog4.setView(name);
                                    dialog4.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addCollection(name.getText().toString(), questionItem.getUserId(), questionItem.getQuestionId());
                                        }
                                    });
                                    dialog4.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog4.show();
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

                    if (questionItem.isFlag()){
                        popupMenu.getMenu().findItem(R.id.hide).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.open).setVisible(true);
                    }else {
                        popupMenu.getMenu().findItem(R.id.hide).setVisible(true);
                        popupMenu.getMenu().findItem(R.id.open).setVisible(false);
                    }

                }
            });

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    QuestionItem questionItem = mQuestionItems.get(position);
                    Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                    intent.putExtra("keyId", questionItem.getQuestionId());
                    intent.putExtra("label", "Question");
                    intent.putExtra("userId", questionItem.getUserId());
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

            if (!questionItem.getQuestionImgs().isEmpty()){
                List<String> imgPaths = new ArrayList<>();
                for (String imgPath : questionItem.getQuestionImgs()){
                    imgPaths.add(mContext.getResources().getString(R.string.server_ip) + "image/" + questionItem.getUserId() + "/" + imgPath);
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

    private void deleteMyQuestion(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteQuestionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = CONTROL_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();

                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void hideMyQuestion(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateQuestionFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .add("flag", "true")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = CONTROL_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();

                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void openMyQuestion(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateQuestionFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
                .add("flag", "false")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = CONTROL_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();

                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void addCollection(String name, String userId, String keyId) {
        String sendTime = TimeCapture.getChinaTime();
        String address = mContext.getResources().getString(R.string.server_ip) + "newCollectionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("userId", userId)
                .add("collectionTime", sendTime)
                .add("keyId", keyId)
                .add("label", "Question")
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = CONTROL_FAIL;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();

                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);
                }
            }
        });
    }
}
