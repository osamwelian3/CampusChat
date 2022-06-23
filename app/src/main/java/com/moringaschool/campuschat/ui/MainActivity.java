package com.moringaschool.campuschat.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moringaschool.campuschat.Adapters.MessagesAdapter;
import com.moringaschool.campuschat.Models.Message;
import com.moringaschool.campuschat.R;
import com.moringaschool.campuschat.Utilities.MemoryData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private final List<Message> messageList = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl("https://campuschat-6075f-default-rtdb.firebaseio.com/");

    private String name;
    private String phone;
    private String email;

    @BindView(R.id.messageRecyclerView)
    RecyclerView messagesRecycler;
    @BindView(R.id.userProfilePic)
    CircleImageView userProfilePic;

    private String lastMessage = "";
    private int unseenMessage = 0;
    private String chatKey = "";

    private boolean dataSet = false;

    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");

        messagesRecycler.setHasFixedSize(true);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessagesAdapter(messageList, MainActivity.this);
        messagesRecycler.setAdapter(messagesAdapter);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(phone).hasChild("profile_pic")) {
                    final String profilePicUrl = dataSnapshot.child("users").child(phone).child("profile_pic").getValue(String.class);
                    Log.e("Profilepicurl", profilePicUrl);
                    if (!profilePicUrl.toString().isEmpty()) {
                        Picasso.get().load(profilePicUrl).into(userProfilePic);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                unseenMessage = 0;
                lastMessage = "";
                chatKey = "";
                for (DataSnapshot snapshot : dataSnapshot.child("users").getChildren()){
                    final String getMobile = snapshot.getKey();

                    dataSet = false;

                    if (!getMobile.equals(phone)){
                        final String getName = snapshot.child("name").getValue(String.class);
                        final String getProfilePic = snapshot.child("profile_pic").getValue(String.class);

                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int getChatsCount = (int) dataSnapshot.getChildrenCount();

                                if (getChatsCount > 0){
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                        final String getKey = snapshot1.getKey();
                                        chatKey = getKey;

                                        if (snapshot1.hasChild("user_1")  && snapshot1.hasChild("user_2") && snapshot1.hasChild("messages")){
                                            final String getUserOne = snapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = snapshot1.child("user_2").getValue(String.class);

                                            if ((getUserOne.equals(getMobile) && getUserTwo.equals(phone)) || (getUserOne.equals(phone) && getUserTwo.equals(getMobile))){
                                                for (DataSnapshot chatDataSnapshot: snapshot1.child("messages").getChildren()){
                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    final long getLastSeenMessages = Long.parseLong(MemoryData.getLastMessage(MainActivity.this, getKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMessageKey > getLastSeenMessages){
                                                        unseenMessage++;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }

                                if (!dataSet){
                                    dataSet = true;
                                    Message message = new Message(getName, getMobile, lastMessage, unseenMessage, getProfilePic, chatKey);
                                    messageList.add(message);
                                    messagesAdapter.updateData(messageList);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}