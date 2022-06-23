package com.moringaschool.campuschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moringaschool.campuschat.Models.Chat;
import com.moringaschool.campuschat.R;
import com.moringaschool.campuschat.Utilities.MemoryData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> chatList;
    private final Context context;
    private String userMobile;

    public ChatAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        this.userMobile = MemoryData.getPhone(context);
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        if (chat.getMobile().equals(userMobile)){
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.rcvLayout.setVisibility(View.GONE);

            holder.myMessage.setText(chat.getMessage());
            holder.myMsgTime.setText(chat.getDate() + " " + chat.getTime());
        } else {
            holder.myLayout.setVisibility(View.GONE);
            holder.rcvLayout.setVisibility(View.VISIBLE);

            holder.rcvMessage.setText(chat.getMessage());
            holder.rcvMsgTime.setText(chat.getDate() + " " + chat.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateChatList(List<Chat> chatList){
        this.chatList = chatList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rcvLayout)
        LinearLayout rcvLayout;
        @BindView(R.id.myLayout)
        LinearLayout myLayout;
        @BindView(R.id.rcvMessage)
        TextView rcvMessage;
        @BindView(R.id.rcvMsgTime)
        TextView rcvMsgTime;
        @BindView(R.id.myMessage)
        TextView myMessage;
        @BindView(R.id.myMsgTime)
        TextView myMsgTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
