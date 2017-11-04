package com.ilavista.minsksale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyReceiver extends BroadcastReceiver {

    public final static String RECEIVER_MESSAGE_ID = "com.ilavista.minsksale.TYPE";

    private List<MyEvent> events;
    private List<String> names;
    private Boolean isThereIsNewEvents = false;

    public MyReceiver() {
        events = new ArrayList<>();
        names = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean isEnabled;
        Boolean isByTime = intent.getBooleanExtra(SubscriptionManager.NOTIFICATION_BY_TIME,false);
        Boolean isByReboot = ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()));
        if (isByReboot){
            isEnabled = true;
            Log.d("MyLog(MyReceiver)", "Receiver started after reboot");
        }
        else
        if (isByTime) isEnabled = true;
        else isEnabled = ProgramConfigs.getInstance(context).isInternetReceiverEnabled();
        if (isEnabled) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                Log.d("MyLog(MyReceiver)", "We've got INTERNET!");
                LoadDataFromDB(context, events);
                getEventsNames(events, names);

                DownloadDataTask downloadTask = new DownloadDataTask(context, events);
                downloadTask.execute();

                // resetting the receiver
                SubscriptionManager.setNotifications(context, ProgramConfigs.getInstance(context).getNotificationPeriod());
                ProgramConfigs.getInstance(context).disableInternetReceiver(context);
            } else {
                Log.d("MyLog(MyReceiver)", "We don't have INTERNET!");
                ProgramConfigs.getInstance(context).enableInternetReceiver(context);
            }
        }
    }

    private class DownloadDataTask extends AsyncTask<String,Integer,String> {

        private Context context;
        private List<MyEvent> events;

        public DownloadDataTask(Context context, List<MyEvent> events) {
            this.events = events;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String URL = ProgramConfigs.getInstance(context).getDataURL();
            Log.d("MyLog(MyReceiver)","Trying to download data from: " + URL);
            InputStream input = null;
            HttpURLConnection connection = null;

            try {
                java.net.URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("logf", "Get error report: " + "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();
                JSONDataLoader dataLoader = new JSONDataLoader();
                events = dataLoader.getEventsFromJson(input);
                Log.d("MyLog(MyReceiver)", "Loaded events: " + events.size());
            } catch (Exception e) {
                Log.d("MyLog(MyReceiver)","Get exception: " + e.toString());
                return e.toString();
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification;
            int index = 0;
            List<String> listOfSubscriptions = new SubscriptionManager(context).getAll();
            for (MyEvent event:events){
                if (!isEventInDB(event)) {
                    isThereIsNewEvents = true;
                    for (String str : listOfSubscriptions) {
                        if (str.equals(event.getOrganizer())) {
                            Intent intent = new Intent(context,MainActivity.class);
                            intent.putExtra(RECEIVER_MESSAGE_ID,event.getID());
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                notification = new Notification.Builder(context)
                                        .setContentTitle("Новое событие от " + event.getOrganizer())
                                        .setContentText(event.getName())
                                        .setSmallIcon(R.drawable.notification_image)
                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_image_large))
                                        .setLights(Color.YELLOW, 3000, 3000)
                                        .setVibrate(new long[]{0, 300, 100, 300})
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true).build();
                            } else {
                                notification = new NotificationCompat.Builder(context)
                                        .setContentTitle("Новое событие от " + event.getOrganizer())
                                        .setContentText(event.getName())
                                        .setSmallIcon(R.drawable.notification_image)
                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_image_large))
                                        .setLights(Color.YELLOW, 3000, 3000)
                                        .setVibrate(new long[]{0, 300, 100, 300})
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true).build();
                            }
                            notificationManager.notify(index, notification);
                        }
                        index++;
                    }
                }
            }
            if (isThereIsNewEvents) {
                Log.d("MyLog(MyReceiver)", "We have " + index + " new events");
                loadDataInDB(context,events);
            }
            else Log.d("MyLog(MyReceiver)", "We have no new events");

        }

    }

    // My functions -------------------
    public void loadDataInDB(Context context, List<MyEvent> events){
        DBManager dbManager = new DBManager(context,"Events");
        dbManager.loadInDB(events);
    }
    //------------------------------------------------------------------------------------
    void LoadDataFromDB(Context context, List<MyEvent> events){
        DBManager dbManager;
        dbManager = new DBManager(context,"Events");
        dbManager.loadFromDBInMainThread(events,"All",null);
    }
    void getEventsNames(List<MyEvent> events,List<String> names){
        for (MyEvent event:events)
            names.add(event.getName());
    }

    boolean isEventInDB(MyEvent event){
        String name = event.getName();
        for (String str: names){
            if (str.equals(name))
                return true;
        }
        return false;
    }
}
