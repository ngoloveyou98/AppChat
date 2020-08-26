package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.MessengeActivity;
import com.example.appchat.Model.User;
import com.example.appchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mConText;
    private List<User> mUser;
    private boolean isChat;
    public UserAdapter(Context mConText, List<User> mUser, boolean isChat) {
        this.mConText = mConText;
        this.mUser = mUser;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mConText).inflate(R.layout.user_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUser.get(position);
        holder.mUsername.setText(user.getUsername());
        if(user.getImageUrl().matches("default")){
            if (user.getSex().equals("Nam")){
                holder.mImgProfile.setImageResource(R.mipmap.ic_male);
            }else{
                holder.mImgProfile.setImageResource(R.mipmap.ic_female);
            }
        }else Glide.with(mConText).load(user.getImageUrl()).into(holder.mImgProfile);
        long timeOff = (System.currentTimeMillis() - user.getTimeOff())/60000;

        if (isChat){
            if (user.getStatus().equals("online")){
                holder.mImgOn.setVisibility(View.VISIBLE);
                holder.mImgOff.setVisibility(View.GONE);
            }else {
                if ( timeOff < 60 && timeOff > 2){
                    holder.mTxtTimeOff.setText(timeOff + " Phút");
                    holder.mTxtTimeOff.setVisibility(View.VISIBLE);
                }else{
                    holder.mTxtTimeOff.setVisibility(View.GONE);
                }
                holder.mImgOn.setVisibility(View.GONE);
                holder.mImgOff.setVisibility(View.GONE);
            }
        }else{
            holder.mImgOn.setVisibility(View.GONE);
            holder.mImgOff.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mConText, MessengeActivity.class);
                intent.putExtra("userid", user.getUserid());
                mConText.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mUsername , mTxtTimeOff;
        public ImageView mImgProfile;
        public CircleImageView mImgOn, mImgOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.username);
            mImgProfile = itemView.findViewById(R.id.profile_image);
            mImgOff     = itemView.findViewById(R.id.circleImgViewOff);
            mImgOn      = itemView.findViewById(R.id.circleImgViewOn);
            mTxtTimeOff = itemView.findViewById(R.id.textViewTimeOff);
        }
    }
}
