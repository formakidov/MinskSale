package com.ilavista.minsksale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ilavistaDB";
    public static final String DATABASE_TABLE_EVENTS = "Events";
    public static final String DATABASE_TABLE_FAVORITE = "MyFavorite";
    public static final String DATABASE_TABLE_SUBSCRIPTION = "Subscription";

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("logf", "onCreate database");
        // создаем таблицу с полями
        db.execSQL("create table Events ("
                + "ID integer primary key autoincrement,"
                + "Type text,"
                + "Rate integer,"
                + "Name text,"
                + "Organizer text,"
                + "StartDate text,"
                + "StartTime text,"
                + "FinishDate text,"
                + "FinishTime text,"
                + "ImageURL text,"
                + "ImageName text,"
                + "Location text,"
                + "Description text" + ");");

        db.execSQL("create table MyFavorite ("
                + "ID integer primary key autoincrement,"
                + "Type text,"
                + "Rate integer,"
                + "Name text,"
                + "Organizer text,"
                + "StartDate text,"
                + "StartTime text,"
                + "FinishDate text,"
                + "FinishTime text,"
                + "ImageURL text,"
                + "ImageName text,"
                + "Location text,"
                + "Description text" + ");");

        db.execSQL("create table Subscription ("
                + "Organizer text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("logf", "onUpgrade database");

        db.execSQL("create table Subscription ("
                + "Organizer text" + ");");
    }

}
