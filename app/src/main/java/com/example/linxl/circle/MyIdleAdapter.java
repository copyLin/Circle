package com.example.linxl.circle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.IdleItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Linxl on 2019/1/18.
 */

public class MyIdleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<IdleItem> mIdleItems;

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
        ImageView mImageView;
        TextView idleName;
        TextView idleContent;
        TextView idlePrice;
        ImageButton moreButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mImageView = (ImageView) view.findViewById(R.id.idle_image);
            idleName = (TextView) view.findViewById(R.id.idle_name);
            idleContent = (TextView) view.findViewById(R.id.idle_content);
            idlePrice = (TextView) view.findViewById(R.id.idle_price);
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

    public MyIdleAdapter(List<IdleItem> idleItems){
        mIdleItems = idleItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_idle, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);

            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    final IdleItem idleItem = mIdleItems.get(position);
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:
                                    android.support.v7.app.AlertDialog.Builder dialog1 = new android.support.v7.app.AlertDialog.Builder(mContext);
                                    dialog1.setMessage("删除后将无法恢复，点击确定删除");
                                    dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteMyIdle(idleItem.getUserId(), idleItem.getSendTime());
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
                                    android.support.v7.app.AlertDialog.Builder dialog2 = new android.support.v7.app.AlertDialog.Builder(mContext);
                                    dialog2.setMessage("点击确定，将问题设为仅自己可见");
                                    dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            hideMyIdle(idleItem.getUserId(), idleItem.getSendTime());
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
                                    android.support.v7.app.AlertDialog.Builder dialog3 = new android.support.v7.app.AlertDialog.Builder(mContext);
                                    dialog3.setMessage("点击确定，将问题设为公开");
                                    dialog3.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            openMyIdle(idleItem.getUserId(), idleItem.getSendTime());
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

                    popupMenu.getMenu().findItem(R.id.like).setVisible(false);
                    if (idleItem.isFlag()){
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
                    IdleItem idleItem = mIdleItems.get(position);
                    Intent intent = new Intent(mContext, IdleDetailActivity.class);
                    intent.putExtra("keyId", idleItem.getIdleId());
                    intent.putExtra("label", "Idle");
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
            ((NormalViewHolder) holder).idlePrice.setText(idleItem.getPrice());
            if (!idleItem.getIdleImgs().isEmpty()) {
                Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/" + idleItem.getUserId() + "/" + idleItem.getIdleImgs().get(0)).into(((NormalViewHolder) holder).mImageView);
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

    private void deleteMyIdle(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteIdleServlet";
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

    private void hideMyIdle(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateIdleFlag";
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

    private void openMyIdle(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateIdleFlag";
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
                .add("label", "Idle")
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
