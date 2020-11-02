package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadImgApi {

    @GET("/user/storage/")
    Call<String> loadImg();
}
