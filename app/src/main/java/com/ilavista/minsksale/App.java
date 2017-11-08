package com.ilavista.minsksale;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import io.realm.Realm;

public class App extends Application {
    private static final String RELEASE_BUILD_TYPE = "release";

    @Override
    public void onCreate() {
        super.onCreate();
        if (RELEASE_BUILD_TYPE.equals(BuildConfig.BUILD_TYPE)) {
            Fabric.with(this, new Crashlytics());
        } else {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this);
        }
        Realm.init(this);
    }
}
