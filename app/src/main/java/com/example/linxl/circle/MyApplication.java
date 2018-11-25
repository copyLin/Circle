package com.example.linxl.circle;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by Linxl on 2018/11/11.
 */

public class MyApplication extends Application{

    private static Context sContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
        LitePal.initialize(sContext);
    }

    public static Context getContext(){
        return sContext;
    }
}
