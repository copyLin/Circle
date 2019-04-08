package com.example.linxl.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linxl.circle.gson.CollectionItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Linxl on 2019/1/19.
 */

public class MyCollectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<CollectionItem> mCollectionItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CONTROL_SUCCESS:
                    Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                    break;
                case CONTROL_FAIL:
                    Toast.makeText(MyApplication.getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        ImageView mImageView;
        TextView collectionName;
        TextView collectionTime;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mImageView = (ImageView) view.findViewById(R.id.collection_label);
            collectionName = (TextView) view.findViewById(R.id.collection_name);
            collectionTime = (TextView) view.findViewById(R.id.collection_time);
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

    public MyCollectionsAdapter(List<CollectionItem> collectionItems){
        mCollectionItems = collectionItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_collection, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    CollectionItem collectionItem = mCollectionItems.get(position);
                    Intent intent = null;
                    switch (collectionItem.getLabel()){
                        case "Question":
                            intent = new Intent(mContext, QuestionDetailActivity.class);
                            intent.putExtra("keyId", collectionItem.getKeyId());
                            intent.putExtra("label", collectionItem.getLabel());
                            break;
                        case "Lost":
                            intent = new Intent(mContext, LostDetailActivity.class);
                            intent.putExtra("keyId", collectionItem.getKeyId());
                            intent.putExtra("label", collectionItem.getLabel());
                            break;
                        case "Idle":
                            intent = new Intent(mContext, IdleDetailActivity.class);
                            intent.putExtra("keyId", collectionItem.getKeyId());
                            intent.putExtra("label", collectionItem.getLabel());
                            break;
                        default:
                            break;
                    }
                    mContext.startActivity(intent);
                }
            });

            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    final CollectionItem collectionItem = mCollectionItems.get(position);
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_delete, popupMenu.getMenu());
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
                                            deleteMyCollection(collectionItem);
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
                    return false;
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
            CollectionItem collectionItem = mCollectionItems.get(position);
            ((NormalViewHolder) holder).collectionName.setText(collectionItem.getCollectionName());
            ((NormalViewHolder) holder).collectionTime.setText(collectionItem.getCollectionTime());
            if (collectionItem.getLabel().equals("Question")){
                ((NormalViewHolder) holder).mImageView.setImageResource(R.drawable.label_question);
            }else if (collectionItem.getLabel().equals("Lost")){
                ((NormalViewHolder) holder).mImageView.setImageResource(R.drawable.label_lost);
            }else if (collectionItem.getLabel().equals("Idle")){
                ((NormalViewHolder) holder).mImageView.setImageResource(R.drawable.label_idle);
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
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount(){
        if (mCollectionItems != null){
            return mCollectionItems.size() + 1;
        }
        return mCollectionItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }

    private void deleteMyCollection(final CollectionItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteCollectionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", String.valueOf(item.getCollectionId()))
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

                    mCollectionItems.remove(item);
                }
            }
        });
    }
}
