package com.example.movieapplication;


import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadProfileApi {

    String MY_URL = "http://192.168.43.139:8000";

    @GET("/profile/load/")
    Call<Profile> loadProfile();
}
