package com.example.appchat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchat.Adapter.ChatAdapter;
import com.example.appchat.Adapter.UserAdapter;
import com.example.appchat.Model.Chat;
import com.example.appchat.Model.ListUsers;
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
import java.util.HashMap;
import java.util.List;


public class ChatsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

//    private UserAdapter mUserAdapter;
    private List<ListUsers> mListUser;
    private List<User> mUser;

    private FirebaseUser mFUser;
    private DatabaseReference mReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerviewChatUser);
        mFUser = FirebaseAuth.getInstance().getCurrentUser();
        mListUser = new ArrayList<>();
        readChatUser();
    }

    private void readChatUser() {

        mReference = FirebaseDatabase.getInstance().getReference("chatlist").child(mFUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ListUsers listUsers = snapshot.getValue(ListUsers.class);
                    mListUser.add(listUsers);
                }
                readChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readChats() {
        mUser = new ArrayList<>();

        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (ListUsers listUsers : mListUser){
                        if (user.getUserid().equals(listUsers.getId())){
                            mUser.add(user);
                        }
                    }
                }
                mChatAdapter = new ChatAdapter(getContext(), mUser,true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(mChatAdapter);
                mChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void  status(String status){
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFUser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("status", status);

        mReference.updateChildren(map);
    }
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");

    }
}

