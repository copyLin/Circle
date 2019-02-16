package com.example.linxl.circle.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Linxl on 2018/11/17.
 */

public class TimeCapture {

    static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getChinaTime(){
        TimeZone mTimeZone = TimeZone.getTimeZone("GMT+8");
        mSimpleDateFormat.setTimeZone(mTimeZone);
        return mSimpleDateFormat.format(Calendar.getInstance().getTime());
    }
}
