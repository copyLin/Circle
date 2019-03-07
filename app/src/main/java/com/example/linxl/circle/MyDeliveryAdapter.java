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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.DeliveryItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;
import com.example.linxl.circle.utils.TimeCapture;

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

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<DeliveryItem> mDeliveryItems;

    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CONTROL_SUCCESS:
                    Toast.makeText(MyApplication.getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
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

            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    final DeliveryItem deliveryItem = mDeliveryItems.get(position);
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
                                            deleteMyDelivery(deliveryItem);
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
                                            hideMyDelivery(deliveryItem);
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
                                            openMyDelivery(deliveryItem);
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
                                default:
                                    break;

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
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                    if (deliveryItem.isFlag()){
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
                    DeliveryItem deliveryItem = mDeliveryItems.get(position);
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
            ((NormalViewHolder) holder).price.setText("¥ " + deliveryItem.getPrice());

            Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/user_img/" + deliveryItem.getUserImg()).into(((NormalViewHolder) holder).mImageView);

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

    private void deleteMyDelivery(final DeliveryItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteDeliveryServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", item.getUserId())
                .add("sendTime", item.getSendTime())
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
                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);

                    mDeliveryItems.remove(item);
                }
            }
        });
    }

    private void hideMyDelivery(final DeliveryItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateDeliveryFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", item.getUserId())
                .add("sendTime", item.getSendTime())
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
                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);

                    item.setFlag(true);
                }
            }
        });
    }

    private void openMyDelivery(final DeliveryItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateDeliveryFlag";
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", item.getUserId())
                .add("sendTime", item.getSendTime())
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
                    Message message = new Message();
                    message.what = CONTROL_SUCCESS;
                    mHandler.sendMessage(message);

                    item.setFlag(false);
                }
            }
        });
    }
}
