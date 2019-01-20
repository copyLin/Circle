package com.example.linxl.circle;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card);

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.user_image);
        TextView userName = (TextView) findViewById(R.id.user_name);
        TextView userId = (TextView) findViewById(R.id.user_id);
        TextView userDepartment = (TextView) findViewById(R.id.user_department);
        TextView userMajor = (TextView) findViewById(R.id.user_major);
        TextView userWords= (TextView) findViewById(R.id.user_words);
        Button sendButton = (Button) findViewById(R.id.button_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
