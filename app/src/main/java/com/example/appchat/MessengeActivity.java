package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.Adapter.MessageAdapter;
import com.example.appchat.Model.Chat;
import com.example.appchat.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengeActivity extends AppCompatActivity {
    private CircleImageView mImgUser;
    private TextView mTxtUsername;
    private Toolbar mToolbar;
    private EditText mEdtSend;
    private ImageButton mBtnSend;


    private FirebaseUser mFUser;
    private DatabaseReference mReference;
    private Intent intent;

    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView mRecyleView;

    private ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenge);
        mFUser = FirebaseAuth.getInstance().getCurrentUser();
        initView();
        setUptoolbar();
        initAction();

    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        mToolbar        = findViewById(R.id.toolbar);
        mTxtUsername    = findViewById(R.id.username);
        mImgUser        = findViewById(R.id.profileImage);
        mEdtSend        = findViewById(R.id.editTextSend);
        mBtnSend        =(ImageButton) findViewById(R.id.imageSend);
        mRecyleView     = findViewById(R.id.recyclerview);

        mRecyleView.setHasFixedSize(true);
        mRecyleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initAction() {
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mTxtUsername.setText(user.getUsername());
                if(user.getImageUrl().equals("default")){
                    mImgUser.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(mImgUser);
                }
                readMessage(mFUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);




        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = mEdtSend.getText().toString().trim();
                if(!mess.equals("")){
                    sendMessage(mFUser.getUid(), userid, mess);
                }else{
                    Toast.makeText(MessengeActivity.this, "Vui long nhap thong tin", Toast.LENGTH_SHORT).show();
                }
                mEdtSend.setText("");


                // add chatlist
                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatlist")
                        .child(mFUser.getUid())
                        .child(userid);

                //Điểm khác biệt giữa addValueEventListener và addListenerForSingleValueEvent đó là addListenerForSingleValueEvent
                // phát hiện thay đổi dữ liệu một lần duy nhất. Những lần thay đổi của dữ liệu tiếp theo sẽ không được lắng nghe.
                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            chatRef.child("id").setValue(userid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void seenMessage(final String userid){
        mReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(mFUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        snapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUptoolbar() {

         setSupportActionBar(mToolbar);
         getSupportActionBar().setTitle("");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(MessengeActivity.this, MainActivity.class)
                         .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
             }
         });
    }
    private void sendMessage(String sender, String receiver , String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMessage(final String myid, final String userid, final String imageurl){
        mChat = new ArrayList<>();

        mReference = FirebaseDatabase.getInstance().getReference("Chats");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){
                        mChat.add(chat);

                    }
                    messageAdapter = new MessageAdapter(MessengeActivity.this, mChat,imageurl);
                    mRecyleView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status) {
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status",status);
        mReference.updateChildren(map);
    }
    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        mReference.removeEventListener(seenListener);
        status("offline");
    }
}
