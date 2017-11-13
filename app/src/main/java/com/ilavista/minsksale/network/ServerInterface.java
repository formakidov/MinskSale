package com.ilavista.minsksale.network;

import com.ilavista.minsksale.database.model.Event;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ServerInterface {

    @Headers("api-version: 1")
    @GET("getdata.php")
    Observable<Response<List<Event>>> events();

}
