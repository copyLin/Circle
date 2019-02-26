package com.example.linxl.circle;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.linxl.circle.gson.ChatItem;
import com.example.linxl.circle.utils.ActivityCollector;
import com.example.linxl.circle.utils.BottomNavigationViewHelper;
import com.example.linxl.circle.utils.SPUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private BottomNavigationView mBottomNavigationView;
    private ViewPager mViewPager;
    private MenuItem mMenuItem;

    private Intent serviceIntent;
    private String userId = (String) SPUtil.getParam(MyApplication.getContext(), SPUtil.USER_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.inflateHeaderView(R.layout.header_drawer_nav);
        CircleImageView userImage = navHeader.findViewById(R.id.user_image);
        TextView userName = navHeader.findViewById(R.id.user_name);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.float_button);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        Glide.with(this).load(getString(R.string.server_ip) + "image/user_img/" + SPUtil.getParam(this, SPUtil.USER_IMG, "")).into(userImage);
        userName.setText((String) SPUtil.getParam(this, SPUtil.USER_NAME, ""));

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
                        break;
                    case R.id.nav_lost:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_idle:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_delivery:
                        mViewPager.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

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
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_homepage:
                        startActivity(new Intent(MainActivity.this, HomepageActivity.class));
                        break;
                    case R.id.my_questions:
                        startActivity(new Intent(MainActivity.this, MyQuestionsActivity.class));
                        break;
                    case R.id.my_lost:
                        startActivity(new Intent(MainActivity.this, MyLostActivity.class));
                        break;
                    case R.id.my_idle:
                        startActivity(new Intent(MainActivity.this, MyIdleActivity.class));
                        break;
                    case R.id.my_delivery:
                        startActivity(new Intent(MainActivity.this, MyDeliveryActivity.class));
                        break;
                    case R.id.my_collections:
                        startActivity(new Intent(MainActivity.this, MyCollectionsActivity.class));
                        break;
                    case R.id.my_comments:
                        startActivity(new Intent(MainActivity.this, MyViewPointActivity.class));
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new QuestionFragment());
        fragments.add(new LostFragment());
        fragments.add(new IdleFragment());
        fragments.add(new DeliveryFragment());
        adapter.setList(fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);

        serviceIntent = new Intent(this, ChatService.class);
        startService(serviceIntent);

        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.linxl.circle.NEW_MESSAGE");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyApplication.getContext());
        localBroadcastManager.registerReceiver(messageReceiver, intentFilter);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        checkMessageState();
    }

    private void checkMessageState(){
        int count = DataSupport.where("toId = ? and flag = ?", userId, "0").count(ChatItem.class);
        if (count == 0) {
            mFloatingActionButton.setImageResource(R.drawable.ic_chat);
        } else {
            mFloatingActionButton.setImageResource(R.drawable.ic_chat_new);
        }
        Log.d("———— MainActivity ————", "messageState:" + count);
    }

    @Override
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
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
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

    @Override
    protected void onRestart(){
        super.onRestart();
        checkMessageState();

        Log.d("————MainActivity————", "activity restart");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopService(serviceIntent);
        ActivityCollector.removeAvtivity(this);

        Log.d("———— MainActivity ————", "activity destroy");
    }


    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            ChatItem item = (ChatItem) intent.getSerializableExtra("new_msg");
            if(item.getFromId().equals(userId)){

            }else {
                mFloatingActionButton.setImageResource(R.drawable.ic_chat_new);
            }
        }
    }
}
