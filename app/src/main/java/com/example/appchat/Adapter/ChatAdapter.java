package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.MessengeActivity;
import com.example.appchat.Model.Chat;
import com.example.appchat.Model.User;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    //    private List<Chat> mChat;
    private List<User> mUser;
    private boolean isChat;
//    private List<String> mListUser;

    private DatabaseReference mReference;
    private FirebaseUser mFBUser;

    public ChatAdapter(Context mContext, List<User> mUser, boolean isChat) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_user_item, parent, false);
        return new ChatAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Chat chat = mChat.get(position);
        final User user = mUser.get(position);
        holder.mUsername.setText(user.getUsername());
        if (user.getImageUrl().matches("default")) {
            if (user.getSex().equals("Nam")) {
                holder.mProfile.setImageResource(R.mipmap.ic_male);
            } else {
                holder.mProfile.setImageResource(R.mipmap.ic_female);
            }
        } else Glide.with(mContext).load(user.getImageUrl()).into(holder.mProfile);


        long timeOff = (System.currentTimeMillis() - user.getTimeOff()) / 60000;

        // check status
        if (isChat) {
            if (user.getStatus().equals("online")) {
                holder.mImgOn.setVisibility(View.VISIBLE);
                holder.mImgOff.setVisibility(View.GONE);
            } else {
                if (timeOff < 60 && timeOff > 2) {
                    holder.mTxtTimeOff.setText(timeOff + " Phút");
                    holder.mTxtTimeOff.setVisibility(View.VISIBLE);
                } else {
                    holder.mTxtTimeOff.setVisibility(View.GONE);
                }
                holder.mImgOn.setVisibility(View.GONE);
                holder.mImgOff.setVisibility(View.GONE);
            }
        } else {
            holder.mImgOn.setVisibility(View.GONE);
            holder.mImgOff.setVisibility(View.GONE);
        }

        readLastMessege(user.getUserid(), holder.mTxtMessage, holder.mTxtName, holder.mUsername);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessengeActivity.class);
                intent.putExtra("userid", user.getUserid());
                mContext.startActivity(intent);
            }
        });
    }

    private void readLastMessege(final String userid, final TextView lastmess, final TextView name, final TextView username) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Chats");

        if (fuser != null) {
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);

                        if (chat.getSender().equals(fuser.getUid()) && chat.getReceiver().equals(userid)) {
                            lastmess.setText(chat.getMessage());
                            name.setText("Bạn: ");
                        } else if (chat.getSender().equals(userid) && chat.getReceiver().equals(fuser.getUid())) {
                            lastmess.setText(chat.getMessage());
                            name.setText("");

                            // check isseen
                            if (!chat.isIsseen()) {
                                lastmess.setTypeface(lastmess.getTypeface(), Typeface.BOLD);
                                username.setTypeface(username.getTypeface(), Typeface.BOLD);
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername, mTxtName, mTxtMessage, mTxtTimeOff;
        public ImageView mProfile;
        public CircleImageView mImgOn, mImgOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.username);
            mProfile = itemView.findViewById(R.id.profile_image);
            mTxtMessage = itemView.findViewById(R.id.textViewLastmessage);
            mTxtName = itemView.findViewById(R.id.textViewUser);
            mImgOn = itemView.findViewById(R.id.circleImgViewOn);
            mImgOff = itemView.findViewById(R.id.circleImgViewOff);
            mTxtTimeOff = itemView.findViewById(R.id.textViewTimeOff);
        }
    }
}
