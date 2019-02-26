package com.example.linxl.circle.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linxl on 2019/2/26.
 */

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeAvtivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
