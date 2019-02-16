package com.example.linxl.circle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linxl.circle.gson.DeliveryItem;
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
 * Created by Linxl on 2019/1/18.
 */

public class MyDeliveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
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
        ImageButton moreButton;

        public NormalViewHolder(View view){
            super(view);
            mCardView = (CardView) view;
            mImageView = (CircleImageView) view.findViewById(R.id.user_image);
            sendTime = (TextView) view.findViewById(R.id.delivery_time);
            deliveryContent = (TextView) view.findViewById(R.id.delivery_content);
            price = (TextView) view.findViewById(R.id.delivery_price);
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

    public MyDeliveryAdapter(List<DeliveryItem> deliveryItems){
        mDeliveryItems = deliveryItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        if (viewType == TYPE_NORMAL){
            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_delivery, parent, false);
            final NormalViewHolder holder = new NormalViewHolder(view);
            int position = holder.getAdapterPosition();
            final DeliveryItem deliveryItem = mDeliveryItems.get(position);
            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext,v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                    dialog.setMessage("删除后将无法恢复，点击确定删除");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteMyDelivery(deliveryItem.getUserId(), deliveryItem.getSendTime());
                                        }
                                    });
                                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
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

                    //popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                }
            });

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserCardActivity.class);
                    intent.putExtra("userId", deliveryItem.getUserId());
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
            DeliveryItem deliveryItem = mDeliveryItems.get(position);
            ((NormalViewHolder) holder).sendTime.setText(deliveryItem.getSendTime());
            ((NormalViewHolder) holder).deliveryContent.setText(deliveryItem.getContent());
            ((NormalViewHolder) holder).price.setText(deliveryItem.getPrice());

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

    private void deleteMyDelivery(String userId, String sendTime) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteDeliveryServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("sendTime", sendTime)
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