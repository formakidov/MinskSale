package com.ilavista.minsksale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadThread extends Thread {

    public final static int FINISH_DOWNLOADING_SUCCESSFULLY = 1;
    public final static int NO_CONNECTION = 2;

    private Context context;
    private Handler mHandlerLeft, mHandlerRight;
    private List<Event> events;
    private List<String> names;
    List<String> listOfImagesOnDevice;

    public DownloadThread(Context context, Handler mHandlerLeft, Handler mHandlerRight) {
        this.context = context;
        this.mHandlerLeft = mHandlerLeft;
        this.mHandlerRight = mHandlerRight;
    }

    public void run() {
        Log.d("logf_Downloading", "Starting DownloadThread");
        listOfImagesOnDevice = getListOfImagesOnDevice();
        String result = DownloadData(Constants.DATA_URL, null);

        if (result != null) {
            Log.d("logf_Downloading", "Main Data is not downloaded");
            if (mHandlerLeft != null) mHandlerLeft.sendEmptyMessage(NO_CONNECTION);
            if (mHandlerRight != null) mHandlerRight.sendEmptyMessage(NO_CONNECTION);
            return;
        }

        checkForSubscriptionEvents();
        loadDataInDB(events);

        // downloading images
        for (Event event : events) {
            //remove image from list of images
            Iterator it = listOfImagesOnDevice.iterator();
            while (it.hasNext()) {
                String item = (String) it.next();
                if (item.equals(event.getImageName()))
                    it.remove();
            }
            //checking is image exists
            File file = new File(context.getFilesDir() + event.getImageName());
            if (!file.exists()) {
                result = DownloadData(event.getImageURL(), event.getImageName());
                if (result != null) {
                    if (mHandlerLeft != null) mHandlerLeft.sendEmptyMessage(NO_CONNECTION);
                    if (mHandlerRight != null) mHandlerRight.sendEmptyMessage(NO_CONNECTION);
                    return;
                }
                Log.d("logf_Downloading", "Image downloaded " + event.getImageName());
            }
        }

        // deleting images
        boolean delete_result = false;
        for (String str : listOfImagesOnDevice) {
            Log.d("logf_Downloading", "Deleting files");
            File picture = new File(context.getFilesDir() + str);
            if (picture.exists()) delete_result = picture.delete();
            if (delete_result) Log.d("logf_Downloading", "File deleted:" + str);
        }

        if (mHandlerLeft != null) mHandlerLeft.sendEmptyMessage(FINISH_DOWNLOADING_SUCCESSFULLY);
        if (mHandlerRight != null) mHandlerRight.sendEmptyMessage(FINISH_DOWNLOADING_SUCCESSFULLY);

    }

    private void loadDataInDB(List<Event> events) {
        DBManager.insert(events);
    }

    private void LoadDataFromDB(List<Event> events) {
        DBManager.loadEvents(events, "All");
    }

    private boolean isEventInDB(Event event) {
        String name = event.getName();
        for (String str : names) {
            if (str.equals(name))
                return true;
        }
        return false;
    }

    private List<String> getListOfImagesOnDevice() {
        List<Event> eventsOnDevice = new ArrayList<>();
        LoadDataFromDB(eventsOnDevice);
        List<String> imageFilesOnDevice = new ArrayList<>();
        for (Event event : eventsOnDevice) {
            imageFilesOnDevice.add(event.getImageName());
        }
        return imageFilesOnDevice;
    }

    @Nullable
    private String DownloadData(String URL, String fileName) {
        Log.d("logf_Downloading", "Trying to download data from: " + URL);
        InputStream input = null;
        OutputStream output;
        HttpURLConnection connection = null;

        try {
            java.net.URL url = new URL(URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("logf_Downloading", "Get error report: " + "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // download the file
            input = connection.getInputStream();
            if (fileName == null) {
                JSONDataLoader dataLoader = new JSONDataLoader();
                events = dataLoader.getEventsFromJson(input);
                Log.d("logf_Downloading", "Loaded events: " + events.size());
            } else {
                output = new FileOutputStream(context.getFilesDir() + fileName);
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            }
        } catch (Exception e) {
            Log.d("logf_Downloading", "Get exception: " + e.toString());
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

    private void checkForSubscriptionEvents() {
        names = new ArrayList<>();
        Boolean isThereIsNewEvents = false;
        List<Event> eventsOld = new ArrayList<>();
        LoadDataFromDB(eventsOld);

        for (Event event : eventsOld)
            names.add(event.getName());

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        int index = 0;
        List<String> listOfSubscriptions = new SubscriptionManager(context).getAll();
        for (Event event : events) {
            if (!isEventInDB(event)) {
                isThereIsNewEvents = true;
                for (String str : listOfSubscriptions) {
                    if (str.equals(event.getOrganizer())) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(MyReceiver.RECEIVER_MESSAGE_ID, event.getID());
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
            Log.d("logf_Downloading", "We have " + index + " new events");
            loadDataInDB(events);
        } else Log.d("logf_Downloading", "We have no new events");
    }
}
