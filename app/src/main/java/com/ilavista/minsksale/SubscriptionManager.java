package com.ilavista.minsksale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ilavista.minsksale.database.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionManager {

    public final static String NOTIFICATION_BY_TIME = "com.ilavista.minsksale.NOTIFICATION_BY_TIME";

    private final static String db_table_name = "Subscription";
    Context context;

    public SubscriptionManager(Context context) {
        this.context = context;
    }

    public List<String> getAll() {
        // TODO: 11/7/17
        String string;
        List<String> subscriptions = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c;
        c = db.query(db_table_name, null, null, null, null, null, null);
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int colOrganizer = c.getColumnIndex("Organizer");

            do {
                string = c.getString(colOrganizer);
                subscriptions.add(string);
            } while (c.moveToNext());
        }
        c.close();

        dbHelper.close();

        return subscriptions;
    }

    public void saveAll(List<String> subscriptions) {
        DBHelper dbHelper = new DBHelper(context);
        // создаем объект для данных
        ContentValues singleValue = new ContentValues();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // удаляем все записи
        db.delete(db_table_name, null, null);

        for (String single_subscription : subscriptions) {
            singleValue.put("Organizer", single_subscription);
            db.insert(db_table_name, null, singleValue);
        }

        dbHelper.close();
    }

    public static void setNotifications(Context context, long time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra(NOTIFICATION_BY_TIME, true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.d("logf", "AlarmManager is set (period: " + time / 1000 + " sec)");
    }

    public void add(String subscription) {
        DBHelper dbHelper = new DBHelper(context);
        // создаем объект для данных
        ContentValues singleValue = new ContentValues();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        singleValue.put("Organizer", subscription);
        db.insert(db_table_name, null, singleValue);

        dbHelper.close();
        Log.d("logf", subscription + " added to Subscriptions");
    }

    public Boolean isInSubscriptions(String subscription) {
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String Query = "Select * from " + db_table_name + " where " + "Organizer=?";
        Cursor cursor = db.rawQuery(Query, new String[]{subscription});
        if (cursor.getCount() <= 0) {
            cursor.close();
            dbHelper.close();
            return false;
        }
        cursor.close();
        dbHelper.close();
        return true;
    }

    public void remove(String subscription) {
        DBHelper dbHelper = new DBHelper(context);
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        subscription = subscription.replaceAll("'", "''");
        db.delete(db_table_name, "Organizer='" + subscription + "'", null);

        dbHelper.close();
        Log.d("logf", subscription + " removed from Subscriptions");
    }
}

