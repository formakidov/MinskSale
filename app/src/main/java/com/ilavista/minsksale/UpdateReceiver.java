package com.ilavista.minsksale;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ilavista.minsksale.activity.MainActivity;
import com.ilavista.minsksale.database.repository.EventsRepository;
import com.ilavista.minsksale.model.Event;
import com.ilavista.minsksale.network.ConnectionManager;
import com.ilavista.minsksale.network.ServerService;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class UpdateReceiver extends BroadcastReceiver {

    public final static String RECEIVER_MESSAGE_ID = "com.ilavista.minsksale.TYPE";
    private static final String INTENT_ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Inject
    ServerService serverService;
    @Inject
    ConnectionManager connectionManager;

    public UpdateReceiver() {
        App.getAppComponent().inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean isEnabled;
        Boolean isByTime = intent.getBooleanExtra(SubscriptionManager.NOTIFICATION_BY_TIME, false);
        Boolean isByReboot = INTENT_ACTION_BOOT_COMPLETED.equals(intent.getAction());
        if (isByReboot) {
            isEnabled = true;
            Log.d("logf(UpdateReceiver)", "Receiver started after reboot");
        } else if (isByTime) {
            isEnabled = true;
        } else {
            isEnabled = ProgramConfigs.getInstance(context).isInternetReceiverEnabled();
        }
        if (isEnabled) {
            // TODO: 11/13/17
            if (connectionManager.isNetworkAvailable()) {
                Log.d("logf(UpdateReceiver)", "We've got INTERNET!");

                List<Event> cachedEvents = EventsRepository.loadAllEvents();
                serverService.events().subscribe(r -> {
                    List<Event> events = r.body();

                    checkSubscriptions(context, events, cachedEvents);
                    EventsRepository.insertAsync(events);
                }, Timber::e);

                // resetting the receiver
                SubscriptionManager.getInstance(context).setNotifications(ProgramConfigs.getInstance(context).getNotificationPeriod());
                ProgramConfigs.getInstance(context).disableInternetReceiver(context);
            } else {
                Log.d("logf(UpdateReceiver)", "We don't have INTERNET!");
                ProgramConfigs.getInstance(context).enableInternetReceiver(context);
            }
        }
    }

    private void checkSubscriptions(Context context, List<Event> events, List<Event> cachedEvents) {
        List<String> subsOrgNames = SubscriptionManager.getInstance(context).getAllOrganizersNames();
        for (Event event : events) {
            if (isEventCached(event, cachedEvents)) continue;
            for (String orgName : subsOrgNames) {
                if (orgName.equals(event.getOrganizer())) {
                    // TODO: 11/16/17 start eventdetailsactivity with back stack
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(RECEIVER_MESSAGE_ID, event.getId());
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // TODO: 11/16/17 move to NotificationUtils
                    String SUBSCRIPTION_EVENT_CHANNEL_ID = "subscription_event_channel_id";
                    Notification notification = new NotificationCompat.Builder(context, SUBSCRIPTION_EVENT_CHANNEL_ID)
                            .setContentTitle("Новое событие от " + event.getOrganizer())
                            .setContentText(event.getName())
                            .setSmallIcon(R.drawable.notification_image)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_image_large))
                            .setLights(Color.YELLOW, 3000, 3000)
                            .setVibrate(new long[]{0, 300, 100, 300})
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true).build();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel notificationChannel = new NotificationChannel(SUBSCRIPTION_EVENT_CHANNEL_ID,
                                context.getString(R.string.subscriptions_events_notification_channel_name), importance);
                        notificationChannel.enableVibration(true);
                        notificationChannel.enableLights(true);
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (nm != null) {
                            nm.createNotificationChannel(notificationChannel);
                        }
                    }
                    NotificationManagerCompat.from(context).notify((int) event.getId(), notification);

                }
            }
        }
    }

    boolean isEventCached(Event event, List<Event> cachedEvents) {
        for (Event e : cachedEvents) {
            if (e.getId() == event.getId()) {
                return true;
            }
        }
        return false;
    }
}