package com.example.linxl.circle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.gson.ContactItem;
import com.example.linxl.circle.utils.SPUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Linxl on 2018/11/12.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{

    private Context mContext;
    private List<ContactItem> mContactItems;

    class ViewHolder extends RecyclerView.ViewHolder{
        View contactView;
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
                ContactItem item = mContactItems.get(position);
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("fromId", (String) SPUtil.getParam(mContext, SPUtil.USER_ID, ""));
                intent.putExtra("toId", item.getContactId());
                intent.putExtra("contactImg", item.getContactImg());
                mContext.startActivity(intent);
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
            holder.msgCount.setVisibility(View.INVISIBLE);
        } else if (count >99) {
            holder.msgCount.setText("…");
            holder.msgCount.setVisibility(View.VISIBLE);
        } else {
            holder.msgCount.setText("" + count);
            holder.msgCount.setVisibility(View.VISIBLE);
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
}
