package com.moringaschool.campuschat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moringaschool.campuschat.Adapters.ChatAdapter;
import com.moringaschool.campuschat.Models.Chat;
import com.moringaschool.campuschat.R;
import com.moringaschool.campuschat.Utilities.MemoryData;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.c_profilePic)
    CircleImageView profilePic;
    @BindView(R.id.uname)
    TextView name;
    @BindView(R.id.messageInput)
    EditText message;
    @BindView(R.id.sendBtn)
    ImageView sendButton;
    @BindView(R.id.backBtn)
    ImageView backButton;
    @BindView(R.id.chattingRecycler)
    RecyclerView chattingRecycler;

    private String chatKey;
    String getUserMobile = "";
    String getMobile = "";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://campuschat-6075f-default-rtdb.firebaseio.com/");

    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        final String getName = getIntent().getStringExtra("name");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        getMobile = getIntent().getStringExtra("phone");

        getUserMobile = MemoryData.getPhone(this);
        name.setText(getName);
        if (!getProfilePic.isEmpty()) {
            Picasso.get().load(getProfilePic).into(profilePic);
        }

        chattingRecycler.setHasFixedSize(true);
        chattingRecycler.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter = new ChatAdapter(chatList, this);
        chattingRecycler.setAdapter(chatAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (chatKey.isEmpty()) {
                    chatKey = "1";
                    if (dataSnapshot.hasChild("chat")) {
                        chatKey = String.valueOf((dataSnapshot.child("chat").getChildrenCount() + 1));
                    }
                }

                if (dataSnapshot.hasChild("chat")){
                    if (dataSnapshot.child("chat").child(chatKey).hasChild("messages")){
                        chatList.clear();
                        for (DataSnapshot messagesSnapShot : dataSnapshot.child("chat").child(chatKey).child("messages").getChildren()){
                            if (messagesSnapShot.hasChild("msg") && messagesSnapShot.hasChild("mobile")){

                                final String messageTimeStamp = messagesSnapShot.getKey();
                                final String mobile = messagesSnapShot.child("mobile").getValue(String.class);
                                final String getMsg = messagesSnapShot.child("msg").getValue(String.class);

                                Timestamp timestamp = new Timestamp(Long.parseLong(messageTimeStamp));
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                Chat chat = new Chat(getMobile, getName, getMsg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                chatList.add(chat);
                                if (loadingFirstTime | Long.parseLong(messageTimeStamp) > Long.parseLong(MemoryData.getLastMessage(ChatActivity.this, chatKey))){
                                    loadingFirstTime = false;
                                    MemoryData.saveLastMessage(messageTimeStamp, chatKey, ChatActivity.this);
                                    chatAdapter.updateChatList(chatList);
                                    chattingRecycler.scrollToPosition(chatList.size()-1);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == backButton) {
            finish();
        }

        if (v == sendButton) {
            final String getTextMessage = message.getText().toString();

            // current Time
            final String currentTimeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);


            databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUserMobile);
            databaseReference.child("chat").child(chatKey).child("user_2").setValue(getMobile);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("msg").setValue(getTextMessage);
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("mobile").setValue(getUserMobile);

            message.setText("");
        }
    }
}