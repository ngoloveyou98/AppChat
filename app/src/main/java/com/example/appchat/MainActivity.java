package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appchat.Adapter.PagerAdapte;
import com.example.appchat.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mReference;
    private FirebaseUser mUser;

    private CircleImageView mProfileImg;
    private TextView mTxtUsername;
    private Toolbar mToolBar;

    private ViewPager mViewPaper;
    private TabLayout mTabLayout;
    PagerAdapte mPagerAdapter;

//    private FragmentPagerAdapter mViewPagerAdaper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupToolbar();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        initData();
        addViewPagerAdapter();
        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });
    }


    private void initView() {
        mProfileImg     = findViewById(R.id.profileImage);
        mTxtUsername    = findViewById(R.id.username);
        mToolBar        = findViewById(R.id.toolbar);
        mViewPaper      = findViewById(R.id.viewPaper);
        mTabLayout    = findViewById(R.id.tab_Layout);

    }

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                return true;
        }
        return false;
    }
    private void initData(){
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mTxtUsername.setText(user.getUsername());
                if(user.getImageUrl().matches("default")){
                    if (user.getSex().equals("Nam")){
                        mProfileImg.setImageResource(R.mipmap.ic_male);
                    }else{
                        mProfileImg.setImageResource(R.mipmap.ic_female);
                    }
                }else Glide.with(getApplicationContext()).load(user.getImageUrl()).into(mProfileImg);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addViewPagerAdapter(){
        FragmentManager manager = getSupportFragmentManager();
        mPagerAdapter = new PagerAdapte(manager);
        mViewPaper.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPaper);
    }

    private void status(String status) {
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status",status);
        mReference.updateChildren(map);
    }

    private void timeOff(long time){
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        HashMap<String , Object> map = new HashMap<>();
        map.put("timeOff", time);
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

    @Override
    protected void onStop() {
        super.onStop();
        timeOff(System.currentTimeMillis());
    }
}
