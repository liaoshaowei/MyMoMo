package com.liao.momo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Action on 2016/10/24.
 */
public class SharedPreferencesUtils {
    public static String getSPData(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public static boolean saveSPData(String key, String info, Context context) {
        SharedPreferences pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return pref.edit().putString(key, info).commit();
    }
}