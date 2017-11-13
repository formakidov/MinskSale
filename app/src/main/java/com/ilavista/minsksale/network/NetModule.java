package com.ilavista.minsksale.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ilavista.minsksale.CacheManager;
import com.ilavista.minsksale.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class NetModule {
    private Context context;

    public NetModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    ConnectionManager providesConnectionManager() {
        return new ConnectionManager(context);
    }

    @Provides
    @Singleton
    ServerService providesServerService(@Named("server") Retrofit retrofit,
                                        ConnectionManager connectionManager,
                                        CacheManager cache) {
        ServerInterface server = retrofit.create(ServerInterface.class);
        return new ServerService(connectionManager, server, cache);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    @Singleton
    @Named("server")
    OkHttpClient provideOkHttpClientServer() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS);

        return clientBuilder.build();
    }

    @Provides
    @Singleton
    @Named("server")
    Retrofit provideRetrofitServer(Gson gson, @Named("server") OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }
}
