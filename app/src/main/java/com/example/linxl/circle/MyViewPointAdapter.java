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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ViewPointItem;
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
 * Created by Linxl on 2019/1/20.
 */

public class MyViewPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOTER = 1;
    static final int LOADING_MORE = 0;
    static final int NO_MORE = 1;

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;

    private int footer_state = 0;
    private Context mContext;
    private List<ViewPointItem> mViewPointItems;

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

    public MyViewPointAdapter(List<ViewPointItem> viewPointItems){
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

            holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    final ViewPointItem viewPointItem = mViewPointItems.get(position);
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
                                            deleteMyViewPoint(viewPointItem);
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
        if (mViewPointItems != null){
            return mViewPointItems.size() + 1;
        }
        return mViewPointItems.size();
    }

    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }

    private void deleteMyViewPoint(final ViewPointItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "deleteViewPointServlet";
        RequestBody requestBody = new FormBody.Builder()
                .add("id", item.getViewPointId())
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

                    mViewPointItems.remove(item);
                }
            }
        });
    }
}
