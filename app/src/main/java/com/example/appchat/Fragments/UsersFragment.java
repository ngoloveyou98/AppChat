package com.example.appchat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.appchat.Adapter.UserAdapter;
import com.example.appchat.Model.User;
import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<User> mUsers = new ArrayList<>();
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mReference;
    private FirebaseUser mFUser;
    private UserAdapter mUserAdapter;

    private EditText mEdtSearchUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView    = view.findViewById(R.id.recycler_view);
        mEdtSearchUser  = view.findViewById(R.id.editTextSearchUser);
        mFUser = FirebaseAuth.getInstance().getCurrentUser();
        readUser();

        mEdtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchUser(String s) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                    .startAt(s).endAt(s + "\uf8ff");
//        endAt(String s // gia tri ket thuc) :Tạo một truy vấn bị ràng buộc để chỉ trả về các nút con có giá trị nhỏ hơn hoặc bằng giá trị đã cho,
//                      sử dụng chỉ thị hoặc ưu tiên orderBy đã cho làm mặc định.
//        startAt(String s // gia tri bat dau)
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getUserid().equals(fuser.getUid())){
                        mUsers.add(user);
                    }
                }
                mUserAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readUser() {
        mFirebaseUser   = FirebaseAuth.getInstance().getCurrentUser();
        mReference      = FirebaseDatabase.getInstance().getReference("Users");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snnapshot : dataSnapshot.getChildren()){
                    User user = snnapshot.getValue(User.class);
                    assert user != null;
                    if(!user.getUserid().equals(mFirebaseUser.getUid())){
                        mUsers.add(user);
                    }
                }

                mUserAdapter = new UserAdapter(getContext(), mUsers,true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(mUserAdapter);
                mUserAdapter.notifyDataSetChanged();
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
        status("offline");
    }
}
