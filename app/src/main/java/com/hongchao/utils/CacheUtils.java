package com.hongchao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hongchao.activity.GuideActivity;

//作用：缓存软件的一些数据和参数
public class CacheUtils {
    //得到缓存值
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("hongchao",context.MODE_PRIVATE);
        return sp.getBoolean(key, false);

    }
    //保存参数值
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("hongchao",context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
}
