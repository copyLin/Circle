package com.example.linxl.circle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.linxl.circle.utils.BottomNavigationViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private BottomNavigationView mBottomNavigationView;
    private ViewPager mViewPager;
    private MenuItem mMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.float_button);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
        }
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_edit));

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                mFloatingActionButton.setImageResource(R.drawable.ic_chat);
            }
        });

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mMenuItem = item;
                switch (item.getItemId()){
                    case R.id.nav_question:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.nav_lost:
                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.nav_idle:
                        mViewPager.setCurrentItem(2);
                        return true;
                    case R.id.nav_delivery:
                        mViewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new QuestionFragment());
        fragments.add(new LostFragment());
        fragments.add(new IdleFragment());
        fragments.add(new DeliveryFragment());
        adapter.setList(fragments);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mMenuItem != null){
                    mMenuItem.setChecked(false);
                }else {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mMenuItem = mBottomNavigationView.getMenu().getItem(position);
                mMenuItem.setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_user:
                        break;
                    case R.id.nav_collections:
                        break;
                    case R.id.nav_info:
                        break;
                    case R.id.nav_setup:
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search_message:
                Toast.makeText(this, "点击搜索", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.new_question:
                startActivity(new Intent(MainActivity.this, NewQuestionActivity.class));
                break;
            case R.id.new_lost:
                startActivity(new Intent(MainActivity.this, NewLostActivity.class));
                break;
            case R.id.new_idle:
                startActivity(new Intent(MainActivity.this, NewIdleActivity.class));
                break;
            case R.id.new_delivery:
                startActivity(new Intent(MainActivity.this, NewDeliveryActivity.class));
                break;
            default:
        }
        return true;
    }
}
