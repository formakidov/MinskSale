package com.ilavista.minsksale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ProgramConfigs {

    private final static String PREFERENCES_GET_PERIOD = "com.ilavista.minsksale.PREFERENCES_PERIOD";
    private final static String PREFERENCES_GET_INTERNET_RECEIVER = "com.ilavista.minsksale.PREFERENCES_INTERNET_RECEIVER";

    private String dataURL;
    private int notificationPeriod;
    private Boolean isInternetReceiverEnabled;
    private Boolean isFirstStart;

    private static ProgramConfigs ourInstance;

    public static ProgramConfigs getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new ProgramConfigs(context);

        return ourInstance;
    }

    private ProgramConfigs(Context context) {
        SharedPreferences sp = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        dataURL = "http://minsksale.by/getdata.php";
        notificationPeriod = sp.getInt(PREFERENCES_GET_PERIOD,12*60*60*1000);
        isInternetReceiverEnabled = sp.getBoolean(PREFERENCES_GET_INTERNET_RECEIVER, false);
        isFirstStart = true;
        Log.d("MyLog(ProgramConfigs)", "Loaded notificationPeriod: " + notificationPeriod / 1000 + " sec");
    }

    public void save(Context context){
        SharedPreferences sp = context.getSharedPreferences("Preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(PREFERENCES_GET_PERIOD, notificationPeriod);
        ed.putBoolean(PREFERENCES_GET_INTERNET_RECEIVER,isInternetReceiverEnabled);
        ed.apply();
    }

    public Boolean isFirstStart() {
        if (isFirstStart) {
            isFirstStart = false;
            return true;
        }
        else{
            return false;
        }

    }

    public String getDataURL() {
        return dataURL;
    }

    public int getNotificationPeriod() {
        return notificationPeriod;
    }

    public void setDataURL(String dataURL) {
        this.dataURL = dataURL;
    }

    public Boolean isInternetReceiverEnabled() {
        return isInternetReceiverEnabled;
    }

    public void enableInternetReceiver(Context context) {
        this.isInternetReceiverEnabled = true;
        save(context);
        Log.d("MyLog(ProgramConfigs)","Internet Receiver enabled");
    }

    public void disableInternetReceiver(Context context) {
        this.isInternetReceiverEnabled = false;
        save(context);
        Log.d("MyLog(ProgramConfigs)", "Internet Receiver disabled");
    }

    public void setNotificationPeriod(int notificationPeriod) {
        this.notificationPeriod = notificationPeriod;
    }
}
