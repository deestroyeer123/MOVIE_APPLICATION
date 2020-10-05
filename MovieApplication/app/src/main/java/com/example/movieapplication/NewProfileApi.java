package com.example.movieapplication;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewProfileApi {

    String MY_URL = "http://192.168.0.3:8000/profile/";

    @POST("create/")
    Call<RequestBody> addProfile(@Body Profile profile);

}
