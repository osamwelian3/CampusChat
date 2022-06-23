package com.moringaschool.campuschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moringaschool.campuschat.Models.Message;
import com.moringaschool.campuschat.R;
import com.moringaschool.campuschat.ui.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Message> messageList;
    private final Context context;

    public MessagesAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (!message.getProfilePic().isEmpty()){
            Picasso.get().load(message.getProfilePic()).into(holder.profilePicture);
        }
        holder.name.setText(message.getName());
        holder.lastMessage.setText(message.getLastMessage());

        if (message.getUnseenMessage() == 0){
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        } else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(message.getUnseenMessage()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_color_80));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("phone", message.getMobile());
                intent.putExtra("name", message.getName());
                intent.putExtra("profile_pic", message.getProfilePic());
                intent.putExtra("chat_key", message.getChatKey());

                context.startActivity(intent);
            }
        });
    }

    public void updateData(List<Message> messageList){
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profilePicture)
        CircleImageView profilePicture;
        @BindView(R.id.m_name)
        TextView name;
        @BindView(R.id.lastMessage)
        TextView lastMessage;
        @BindView(R.id.unseenMessage)
        TextView unseenMessages;
        @BindView(R.id.rootLayout)
        LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
