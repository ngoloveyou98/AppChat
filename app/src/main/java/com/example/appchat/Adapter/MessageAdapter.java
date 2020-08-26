package com.example.appchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Model.Chat;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT =0;
    private static final int MSG_TYPE_RIGHT =1;
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    private FirebaseUser mFUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_massage.setText(chat.getMessage());
        if (imageurl.equals("default")){
            holder.mImgProfile.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(imageurl).into(holder.mImgProfile);
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txtSeen.setText("Đã đọc");
            }else{
                holder.txtSeen.setText("Đã gửi");
            }
        }else{
            holder.txtSeen.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_massage , txtSeen;
        public ImageView mImgProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_massage = itemView.findViewById(R.id.show_message);
            mImgProfile = itemView.findViewById(R.id.profile_image);
            txtSeen     = itemView.findViewById(R.id.textViewSeeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mFUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(mFUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
