package com.example.dailyarticle.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by ZZP on 2017/7/5.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        //全局获取context
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
