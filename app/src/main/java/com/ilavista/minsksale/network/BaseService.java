package com.ilavista.minsksale.network;

import android.net.NetworkInfo;
import android.util.Log;

import com.ilavista.minsksale.Constants;
import com.ilavista.minsksale.network.exceptions.NoInternetException;
import com.ilavista.minsksale.network.exceptions.ServerBusyException;
import com.ilavista.minsksale.utils.Rx;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class BaseService {
    protected final ConnectionManager connectionManager;

    protected BaseService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public <T> Observable<T> async(Observable<T> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public <T> Observable<T> backoff(Observable<T> observable) {
        return observable.retryWhen(this::backoffImpl);
    }

    <T> Observable<T> checkNetwork(Observable<T> observable) {
        NetworkInfo info = connectionManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return observable;
        } else {
            return Observable.error(new NoInternetException());
        }
    }

    public <T> Observable<Response<T>> checkServerBusy(Observable<Response<T>> observable) {
        return observable
                .flatMap(response -> {
                    if (response.code() == Constants.HTTP.SERVER_BUSY) {
                        return Observable.error(new ServerBusyException());
                    } else {
                        return Observable.just(response);
                    }
                });
    }

    private Observable<Long> backoffImpl(Observable<? extends Throwable> errors) {
        final int[] repeatCount = {0};
        return errors.zipWith(Observable.range(1, Constants.MAX_NETWORK_RETRIES + 1), (error, i) -> {
            repeatCount[0] = i;
            return error;
        }).flatMap(error -> {
            if (error instanceof ServerBusyException) {
//                if (!((ServerBusyException) error).shouldRetry()) {
//                    return Observable.error(error);
//                }
            }
            if (repeatCount[0] > Constants.MAX_NETWORK_RETRIES) {
                return Observable.error(error);
            }
            Log.d("ExponentialBackoff", "RETRY REQUEST: #" + repeatCount[0]);
            return Observable.timer(error instanceof ServerBusyException ? 3 : 1, TimeUnit.SECONDS);
        });
    }
}
