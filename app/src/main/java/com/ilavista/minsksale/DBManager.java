package com.ilavista.minsksale;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

public class DBManager {

    Context context;
    private String db_table_name;

    public DBManager(Context context, String db_table_name) {
        this.context = context;
        this.db_table_name = db_table_name;
    }

    public void loadInDB(final List<MyEvent> events) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(context);
                // создаем объект для данных
                ContentValues singleValue = new ContentValues();
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                // удаляем все записи
                int clearCount = db.delete(db_table_name, null, null);
                Log.d("MyLog", "Clear table:" + db_table_name);
                Log.d("MyLog", "deleted rows count = " + clearCount);

                for (MyEvent event : events) {
                    singleValue.put("ID", event.getID());
                    singleValue.put("Type", event.getType());
                    singleValue.put("Rate", event.getRate());
                    singleValue.put("Name", event.getName());
                    singleValue.put("Organizer", event.getOrganizer());
                    singleValue.put("StartDate", event.getStartDate());
                    singleValue.put("StartTime", event.getStartTime());
                    singleValue.put("FinishDate", event.getFinishDate());
                    singleValue.put("FinishTime", event.getFinishTime());
                    singleValue.put("ImageURL", event.getImageURL());
                    singleValue.put("ImageName", event.getImageName());
                    singleValue.put("Location", event.getLocation());
                    singleValue.put("Description", event.getDescription());
                    long rowID = db.insert("Events", null, singleValue);
                    Log.d("MyLog", "Row inserted in DB, ID = " + rowID);
                }
                dbHelper.close();
            }
        });
        thread.run();
    }

    public void loadFromDB(final List<MyEvent> events, final String type) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MyEvent singleEvent;
                DBHelper dbHelper = new DBHelper(context);
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                events.clear();
                Cursor c;
                if ((type.equals("All")) || (type.equals("first")) || (type.equals("selected")))
                    c = db.query(db_table_name, null, null, null, null, null, "ID DESC");
                else {
                    String selection = "Type = ?";
                    String[] selectionArgs = new String[]{type};
                    c = db.query(db_table_name, null, selection, selectionArgs, null, null,
                            "ID DESC");
                }

                // ставим позицию курсора на первую строку выборки
                // если в выборке нет строк, вернется false
                if (c.moveToFirst()) {
                    // определяем номера столбцов по имени в выборке
                    int colID = c.getColumnIndex("ID");
                    int colType = c.getColumnIndex("Type");
                    int colRate = c.getColumnIndex("Rate");
                    int colName = c.getColumnIndex("Name");
                    int colOrganizer = c.getColumnIndex("Organizer");
                    int colStartDate = c.getColumnIndex("StartDate");
                    int colStartTime = c.getColumnIndex("StartTime");
                    int colFinishDate = c.getColumnIndex("FinishDate");
                    int colFinishTime = c.getColumnIndex("FinishTime");
                    int colImageURL = c.getColumnIndex("ImageURL");
                    int colImageName = c.getColumnIndex("ImageName");
                    int colLocation = c.getColumnIndex("Location");
                    int colDescription = c.getColumnIndex("Description");

                    do {
                        singleEvent = new MyEvent();
                        singleEvent.setID(c.getInt(colID));
                        singleEvent.setType(c.getString(colType));
                        singleEvent.setRate(c.getInt(colRate));
                        singleEvent.setName(c.getString(colName));
                        singleEvent.setOrganizer(c.getString(colOrganizer));
                        singleEvent.setStartDate(c.getString(colStartDate));
                        singleEvent.setStartTime(c.getString(colStartTime));
                        singleEvent.setFinishDate(c.getString(colFinishDate));
                        singleEvent.setFinishTime(c.getString(colFinishTime));
                        singleEvent.setImageURL(c.getString(colImageURL));
                        singleEvent.setImageName(c.getString(colImageName));
                        singleEvent.setLocation(c.getString(colLocation));
                        singleEvent.setDescription(c.getString(colDescription));
                        events.add(singleEvent);
                    } while (c.moveToNext());
                } else
                    Log.d("MyLog", "0 rows");
                c.close();

                dbHelper.close();

            }
        });
        thread.run();
    }

    public void insertInDB(final MyEvent event) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(context);
                // создаем объект для данных
                ContentValues singleValue = new ContentValues();
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                singleValue.put("ID", event.getID());
                singleValue.put("Type", event.getType());
                singleValue.put("Rate", event.getRate());
                singleValue.put("Name", event.getName());
                singleValue.put("Organizer", event.getOrganizer());
                singleValue.put("StartDate", event.getStartDate());
                singleValue.put("StartTime", event.getStartTime());
                singleValue.put("FinishDate", event.getFinishDate());
                singleValue.put("FinishTime", event.getFinishTime());
                singleValue.put("ImageURL", event.getImageURL());
                singleValue.put("ImageName", event.getImageName());
                singleValue.put("Location", event.getLocation());
                singleValue.put("Description", event.getDescription());
                long rowID = db.insert(db_table_name, null, singleValue);
                Log.d("MyLog", "Row inserted in DB, ID = " + rowID);

                dbHelper.close();
            }
        });
        thread.run();
    }

    public void removeFromDB(final MyEvent event) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(context);
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int i = db.delete(db_table_name, "ID=?", new String[]{Long.toString(event.getID())});
                Log.d("MyLog", "Row removed from MyFavorite, " + i);
                dbHelper.close();
            }
        });
        thread.run();
    }

    public MyEvent getEventFromDB(int ID) {
        MyEvent event = new MyEvent();
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // переменные для query
        String selection;
        String[] selectionArgs;

        selection = "ID = ?";
        selectionArgs = new String[]{String.valueOf(ID)};
        // курсор
        Cursor c = db.query("Events", null, selection, selectionArgs, null, null,
                null);

        if (c != null) {
            if (c.moveToFirst()) {
                event.setID(c.getInt(0));
                event.setType(c.getString(1));
                event.setRate(c.getInt(2));
                event.setName(c.getString(3));
                event.setOrganizer(c.getString(4));
                event.setStartDate(c.getString(5));
                event.setStartTime(c.getString(6));
                event.setFinishDate(c.getString(7));
                event.setFinishTime(c.getString(8));
                event.setImageURL(c.getString(9));
                event.setImageName(c.getString(10));
                event.setLocation(c.getString(11));
                event.setDescription(c.getString(12));
            }
        }

        dbHelper.close();
        return event;
    }

    public Boolean isEventFavorite(int ID) {
        Boolean isFavorite = false;
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // переменные для query
        String selection;
        String[] selectionArgs;

        Cursor c;
        // is Event favorite?
        selection = "ID = ?";
        selectionArgs = new String[]{String.valueOf(ID)};
        // курсор
        c = db.query("MyFavorite", null, selection, selectionArgs, null, null,
                null);
        if (c != null) {
            if (c.moveToFirst()) isFavorite = true;
            c.close();
        }
        return isFavorite;
    }

    public void loadInDBInMainThread(final List<MyEvent> events) {

        DBHelper dbHelper = new DBHelper(context);
        // создаем объект для данных
        ContentValues singleValue = new ContentValues();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // удаляем все записи
        int clearCount = db.delete(db_table_name, null, null);
        Log.d("MyLog", "Clear table:" + db_table_name);

        for (MyEvent event : events) {
            singleValue.put("ID", event.getID());
            singleValue.put("Type", event.getType());
            singleValue.put("Rate", event.getRate());
            singleValue.put("Name", event.getName());
            singleValue.put("Organizer", event.getOrganizer());
            singleValue.put("StartDate", event.getStartDate());
            singleValue.put("StartTime", event.getStartTime());
            singleValue.put("FinishDate", event.getFinishDate());
            singleValue.put("FinishTime", event.getFinishTime());
            singleValue.put("ImageURL", event.getImageURL());
            singleValue.put("ImageName", event.getImageName());
            singleValue.put("Location", event.getLocation());
            singleValue.put("Description", event.getDescription());
            long rowID = db.insert("Events", null, singleValue);
        }
        dbHelper.close();
    }

    public void loadFromDBInMainThread(final List<MyEvent> events, String type, String organizer) {
        MyEvent singleEvent;
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        events.clear();
        Cursor c;
        if (organizer != null) {
            String selection = "Organizer = ?";
            String[] selectionArgs = new String[]{organizer};
            c = db.query(db_table_name, null, selection, selectionArgs, null, null,
                    "ID DESC");
        } else if ((type.equals("All")) || (type.equals("first")) || (type.equals("selected")))
            c = db.query(db_table_name, null, null, null, null, null, "ID DESC");
        else {
            String selection = "Type = ?";
            String[] selectionArgs = new String[]{type};
            c = db.query(db_table_name, null, selection, selectionArgs, null, null,
                    "ID DESC");
        }
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int colID = c.getColumnIndex("ID");
            int colType = c.getColumnIndex("Type");
            int colRate = c.getColumnIndex("Rate");
            int colName = c.getColumnIndex("Name");
            int colOrganizer = c.getColumnIndex("Organizer");
            int colStartDate = c.getColumnIndex("StartDate");
            int colStartTime = c.getColumnIndex("StartTime");
            int colFinishDate = c.getColumnIndex("FinishDate");
            int colFinishTime = c.getColumnIndex("FinishTime");
            int colImageURL = c.getColumnIndex("ImageURL");
            int colImageName = c.getColumnIndex("ImageName");
            int colLocation = c.getColumnIndex("Location");
            int colDescription = c.getColumnIndex("Description");

            do {
                singleEvent = new MyEvent();
                singleEvent.setID(c.getInt(colID));
                singleEvent.setType(c.getString(colType));
                singleEvent.setRate(c.getInt(colRate));
                singleEvent.setName(c.getString(colName));
                singleEvent.setOrganizer(c.getString(colOrganizer));
                singleEvent.setStartDate(c.getString(colStartDate));
                singleEvent.setStartTime(c.getString(colStartTime));
                singleEvent.setFinishDate(c.getString(colFinishDate));
                singleEvent.setFinishTime(c.getString(colFinishTime));
                singleEvent.setImageURL(c.getString(colImageURL));
                singleEvent.setImageName(c.getString(colImageName));
                singleEvent.setLocation(c.getString(colLocation));
                singleEvent.setDescription(c.getString(colDescription));
                events.add(singleEvent);
            } while (c.moveToNext());
        } else
            Log.d("MyLog", "0 rows");
        c.close();

        dbHelper.close();
    }

}
