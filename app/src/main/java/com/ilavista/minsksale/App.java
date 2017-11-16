package com.ilavista.minsksale;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.ilavista.minsksale.dagger.AppComponent;
import com.ilavista.minsksale.dagger.DaggerAppComponent;
import com.ilavista.minsksale.utils.RealmUtils;
import com.ilavista.minsksale.dagger.NetModule;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {
    private static final String RELEASE_BUILD_TYPE = "release";
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (RELEASE_BUILD_TYPE.equals(BuildConfig.BUILD_TYPE)) {
            Fabric.with(this, new Crashlytics());
        } else {
            Timber.plant(new Timber.DebugTree());
            Fabric.with(this);
        }

        RealmUtils.init(this);
//        instantiateFakeMap();

        appComponent = DaggerAppComponent.builder()
                .netModule(new NetModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    // google play services should be instantiate here for better performance of future instantiation
//    private void instantiateFakeMap() {
//        new Thread(() -> {
//            try {
//                MapsInitializer.initialize(this);
//                MapView mv = new MapView(this);
//                mv.onCreate(null);
//                mv.onPause();
//                mv.onDestroy();
//            } catch (Exception ignored) {
//            }
//        }).start();
//    }
}
