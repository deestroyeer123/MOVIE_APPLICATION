package com.example.movieapplication;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface InitializeBaseApi {

    String MY_URL = "http://192.168.0.3:8000";

    @GET("/profile/base/")
    Call<ResponseBody> initialize();
}
