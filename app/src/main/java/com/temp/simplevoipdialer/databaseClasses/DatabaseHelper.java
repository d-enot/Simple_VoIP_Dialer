package com.temp.simplevoipdialer.databaseClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by klim-mobile on 30.08.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "mainDB";
    private static final String DB_PATH = "/data/data/" + DB_NAME + "/databases/";
    private static final String USER_TABLE = "user";
    private static final String CALLS_TABLE = "calls";
    private static final String CONTACTS_TABLE = "contacts";
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + USER_TABLE + " (id INTEGER PRIMARY KAY, login TEXT, password TEXT)");
//        db.execSQL("CREATE TABLE " + CALLS_TABLE + " (id INTEGER PRIMARY KAY AUTOINCREMENT, number TEXT, date TEXT, duration TEXT, type)");
//        db.execSQL("CREATE TABLE " + CONTACTS_TABLE + " (id INTEGER PRIMARY KAY AUTOINCREMENT, first_name TEXT, last_name TEXT, number TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        this.onCreate(db);
    }


}
