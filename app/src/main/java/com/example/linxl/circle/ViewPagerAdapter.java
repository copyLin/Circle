package com.example.linxl.circle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Linxl on 2018/11/25.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;

    public ViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    public void setList(List<Fragment> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position){
        return mList.get(position);
    }

    @Override
    public int getCount(){
        return mList != null ? mList.size() : 0;
    }
}
