package com.example.dailyarticle.BookCase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ZZP on 2017/7/2.
 */

public class ArticleDatabase extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "create table Article("+
            "id integer primary key autoincrement,"+
            "date text,"+
            "Tag text,"+
            "title text,"+
            "author text,"+
            "article text)";

    //public static final String CREATE_PICTURE ;
    //private Context mContext;
    public ArticleDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        Log.w("zzp","aaa");
        //mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.w("zzp","create");
        db.execSQL(CREATE_TABLE);
        Log.w("zzp","succeed create");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists Article");
        db.execSQL("drop table if exists Picture");

    }
}
