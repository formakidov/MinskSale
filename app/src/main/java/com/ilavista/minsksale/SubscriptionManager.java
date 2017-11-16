package com.ilavista.minsksale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ilavista.minsksale.model.Event;
import com.ilavista.minsksale.model.Subscription;
import com.ilavista.minsksale.database.repository.SubscriptionsRepository;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionManager {
    public final static String NOTIFICATION_BY_TIME = "com.ilavista.minsksale.NOTIFICATION_BY_TIME";

    private final Context context;

    private SubscriptionManager(Context context) {
        this.context = context;
    }

    public static SubscriptionManager getInstance(Context context) {
        return new SubscriptionManager(context);
    }

    public void subscribeTo(String orgName) {
        SubscriptionsRepository.insertOrgNamesIfNotExist(orgName);
    }

    public List<String> getAllOrganizersNames() {
        List<String> subscriptionsNames = new ArrayList<>();
        List<Subscription> subs = SubscriptionsRepository.getAll();
        for (Subscription s : subs) {
            subscriptionsNames.add(s.getOrganizerName());
        }
        return subscriptionsNames;
    }

    public boolean isSubscribed(String subscription) {
        return SubscriptionsRepository.hasItem(subscription);
    }

    public void remove(String subscription) {
        SubscriptionsRepository.remove(subscription);
    }

    private boolean isEventInDB(Event event, List<String> names) {
        String name = event.getName();
        for (String str : names) {
            if (str.equals(name))
                return true;
        }
        return false;
    }

    public void setNotifications(long time) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UpdateReceiver.class);
        intent.putExtra(NOTIFICATION_BY_TIME, true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.d("logf", "AlarmManager is set (period: " + time / 1000 + " sec)");
    }
}

