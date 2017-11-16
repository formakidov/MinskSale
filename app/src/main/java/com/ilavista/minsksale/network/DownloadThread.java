package com.ilavista.minsksale.network;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ilavista.minsksale.Constants;
import com.ilavista.minsksale.database.repository.EventsRepository;
import com.ilavista.minsksale.model.Event;

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
        EventsRepository.insert(events);
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
        List<Event> eventsOnDevice = EventsRepository.loadAllEvents();
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
}
