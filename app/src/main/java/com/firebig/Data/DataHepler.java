package com.firebig.Data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by FireBig-CH on 16-8-17.
 */
public class DataHepler extends SQLiteOpenHelper {

    private String userPass = "create table useraccount(account varchar(20),password varchar(50),objectid varchar(10))";
    private String newsTable = "create table news(account varchar(20),objectid varchar(20),newscontent varchar(250),newsuser varchar(20),newstime varchar(20),listid int(5),fourword int(1),newsimagename varchar(50),newsimageurl varchar(250),headerimagename varchar(50),headerimageurl varchar(250))";
    private String messageTable = "create table notice(user_account varchar(20),from_account varchar(20),from_name varchar(20),news_id varchar(20),message varchar(250),time varchar(20),type int(2))";

    public DataHepler(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt, DatabaseErrorHandler paramDatabaseErrorHandler) {
        super(paramContext, paramString, paramCursorFactory, paramInt, paramDatabaseErrorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sd) {
        sd.execSQL(userPass);
        sd.execSQL(newsTable);
        sd.execSQL(messageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
