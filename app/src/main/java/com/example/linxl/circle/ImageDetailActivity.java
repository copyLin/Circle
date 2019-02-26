package com.example.linxl.circle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.linxl.circle.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_image_detail);

        Intent intent = getIntent();
        int num = intent.getIntExtra("num", 0);
        int position = intent.getIntExtra("position", 0);
        List<String> pNameList = intent.getStringArrayListExtra("imgPaths");

        final List<ImageDetailFragment> list = new ArrayList<>();
        int count = 0;
        for (String imageName : pNameList) {
            count ++;
            ImageDetailFragment fragment = new ImageDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("num", num);
            bundle.putInt("count", count);
            bundle.putString("imgPath", imageName);
            fragment.setArguments(bundle);

            list.add(fragment);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeAvtivity(this);
    }
}
