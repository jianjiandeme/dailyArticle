package com.example.dailyarticle.utils;

/**
 * Created by ZZP on 2017-07-21.
 */

public class Jstr {
    static {
        System.loadLibrary("Jstr");
    }
    public native String getJniString(String json);
}
