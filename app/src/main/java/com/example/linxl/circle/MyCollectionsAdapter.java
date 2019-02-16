package com.example.linxl.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.CollectionItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private int footer_state = 0;
    private Context mContext;
    private List<CollectionItem> mCollectionItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    class NormalViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        CircleImageView mCircleImageView;
        TextView collectionName;
        TextView collectionTime;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.user_image);
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
            int position = holder.getAdapterPosition();
            final CollectionItem collectionItem = mCollectionItems.get(position);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                            deleteMyCollection(collectionItem.getCollectionId());
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

                    popupMenu.getMenu().findItem(R.id.hide).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.open).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.like).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
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

            Glide.with(mContext).load("").into(((NormalViewHolder) holder).mCircleImageView);

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
        if (mCollectionItems != null){
            return mCollectionItems.size() + 1;
        }
        return mCollectionItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }

    private void deleteMyCollection(String id) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteCollectionServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", id)
                .build();
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    Toast.makeText(mContext, responseData, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}