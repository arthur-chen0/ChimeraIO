package com.jht.chimera.io;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jht.chimera.io.fragment.AudioTestFragment;
import com.jht.chimera.io.fragment.MCUFunctionFragment;
import com.jht.chimera.io.fragment.UartCommTestFragment;

public class CollectionAdapter extends FragmentStateAdapter {
    private static final String TAG = CollectionAdapter.class.getSimpleName();
    private static final String[] TAB_TITLES = new String[]{"MCU Function Test", "Uart Communication Test", "Audio Test"};
    private Activity activity;
//    private ArrayList<Fragment> arrayList = new ArrayList<>();

    public CollectionAdapter(@NonNull FragmentManager fragmentManager,
                             @NonNull Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    public void CollectionAdapter_passActivity(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment: "+position);
        switch (position){
            case 0:
                return new MCUFunctionFragment();
            case 1:
                return new UartCommTestFragment(activity);
            case 2:
                return new AudioTestFragment();
        }

        return null;
    }


    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

//    public void addFragment(Fragment fragment) {
//        arrayList.add(fragment);
//    }

    @Override
    public int getItemCount() {
        return TAB_TITLES.length;
    }


}
