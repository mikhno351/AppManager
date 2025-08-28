package com.mikhno.appmanager.adapter.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mikhno.appmanager.fragment.details.BaseFragment;
import com.mikhno.appmanager.fragment.details.DetailsFragment;

public class DetailsAdapter extends FragmentStateAdapter {

    public DetailsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new BaseFragment();
        else return new DetailsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
