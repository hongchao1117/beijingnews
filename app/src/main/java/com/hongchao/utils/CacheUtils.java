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

    /**
     * 缓存文本数据
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("hongchao",context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    /**
     * 获取缓存的文本信息
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("hongchao",context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}
