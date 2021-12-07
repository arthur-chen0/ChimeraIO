package com.jht.chimera.io;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jht.chimera.io.fragment.AudioTestFragment;
import com.jht.chimera.io.fragment.ExtensionPowerTestFragment;
import com.jht.chimera.io.fragment.MCUFunctionFragment;
import com.jht.chimera.io.fragment.UartCommTestFragment;

public class CollectionAdapter extends FragmentStateAdapter {

    private static final String[] TAB_TITLES = new String[]{"MCU Function Test", "Uart Communication Test", "Audio Test"};

//    private ArrayList<Fragment> arrayList = new ArrayList<>();

    public CollectionAdapter(@NonNull FragmentManager fragmentManager,
                             @NonNull Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MCUFunctionFragment();
            case 1:
                return new UartCommTestFragment();
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
