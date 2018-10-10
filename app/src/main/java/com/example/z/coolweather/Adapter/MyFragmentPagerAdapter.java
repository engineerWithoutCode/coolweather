package com.example.z.coolweather.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.z.coolweather.Fragment.CommonFragment;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
    private List<CommonFragment> fragmentList;

    public MyFragmentPagerAdapter(FragmentManager fragmentManager, List<CommonFragment> fragmentList){
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
