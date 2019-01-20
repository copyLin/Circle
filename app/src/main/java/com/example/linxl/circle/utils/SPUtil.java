package com.example.linxl.circle.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Linxl on 2018/11/11.
 */

public class SPUtil {

    public static final String DATA_NAME = "UserData";

    public static final String USER_ID = "userId";

    public static final String USER_NAME = "userName";

    public static final String USER_IMG = "userImg";

    public static final String USER_DEPARTMENT = "userDepartment";

    public static final String USER_MAJOR = "userMajor";

    public static final String IS_LOGIN = "isLogin";


    public static void setParam(Context context, String key, Object object){

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.apply();
    }

    public static Object getParam(Context context, String key, Object defaultObject){

        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }

    public static void removeParam(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }
}
