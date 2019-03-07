package com.example.linxl.circle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.gson.ContactItem;
import com.example.linxl.circle.utils.HttpUtil;
import com.example.linxl.circle.utils.SPUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Linxl on 2018/11/12.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    static final int CONTROL_SUCCESS = 0;
    static final int CONTROL_FAIL = 1;

    private Context mContext;
    private List<ContactItem> mContactItems;

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

    class ViewHolder extends RecyclerView.ViewHolder{
        View contactView;
        View badge;
        CircleImageView mCircleImageView;
        TextView contactName;
        TextView recentMsg;
        TextView msgCount;

        public ViewHolder(View view){
            super(view);
            contactView = view;
            mCircleImageView = (CircleImageView) view.findViewById(R.id.contact_image);
            contactName = (TextView) view.findViewById(R.id.contact_name);
            recentMsg = (TextView) view.findViewById(R.id.recent_msg);
            msgCount = (TextView) view.findViewById(R.id.msg_count);
            badge = (View) view.findViewById(R.id.badge);

        }
    }

    public ContactAdapter(List<ContactItem> contactItems){
        mContactItems = contactItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.msgCount.setVisibility(View.INVISIBLE);
                int position = holder.getAdapterPosition();
                ContactItem contactItem = mContactItems.get(position);
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("fromId", (String) SPUtil.getParam(mContext, SPUtil.USER_ID, ""));
                intent.putExtra("toId", contactItem.getContactId());
                intent.putExtra("contactImg", contactItem.getContactImg());
                intent.putExtra("contactName", contactItem.getContactName());
                mContext.startActivity(intent);
            }
        });

        holder.contactView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                final ContactItem contactItem = mContactItems.get(position);
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_delete, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:
                                AlertDialog.Builder dialog1 = new AlertDialog.Builder(mContext);
                                dialog1.setMessage("将联系人从列表中删除");
                                dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateChatRecordState(contactItem);
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
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        ContactItem item = mContactItems.get(position);
        holder.contactName.setText(item.getContactName());
        holder.recentMsg.setText(item.getRecentChat());
        Glide.with(mContext).load(mContext.getResources().getString(R.string.server_ip) + "image/user_img/" + item.getContactImg()).into(holder.mCircleImageView);

        String fromId = item.getContactId();
        int count = DataSupport.where("fromId = ? and flag = ?", fromId, "0").count(ChatItem.class);
        if (count == 0){
            holder.badge.setVisibility(View.INVISIBLE);
        } else if (count >99) {
            holder.msgCount.setText("…");
            holder.badge.setVisibility(View.VISIBLE);
        } else {
            holder.msgCount.setText("" + count);
            holder.badge.setVisibility(View.VISIBLE);
        }

        Log.d("———ContactAdapter———", "count:" + count);
    }

    @Override
    public int getItemCount(){
        if (mContactItems != null){
            return mContactItems.size();
        }
        return 0;
    }

    private void updateChatRecordState(final ContactItem item) {
        String address = mContext.getResources().getString(R.string.server_ip) + "updateChatRecordState";
        RequestBody requestBody = new FormBody.Builder()
                .add("contactId", item.getContactId())
                .add("userId", userId)
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

                    mContactItems.remove(item);
                }
            }
        });
    }
}
