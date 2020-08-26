package com.example.appchat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchat.Fragments.ChatsFragment;
import com.example.appchat.Fragments.UsersFragment;

public class PagerAdapte extends FragmentPagerAdapter {
    public PagerAdapte(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position){
            case 0:
                frag = new ChatsFragment();
                break;
            case 1:
                frag = new UsersFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Chat";
                break;
            case 1:
                title = "User";
                break;

        }
        return title;
    }
}
