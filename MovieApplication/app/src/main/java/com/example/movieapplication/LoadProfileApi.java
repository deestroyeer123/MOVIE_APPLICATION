package com.example.movieapplication;


import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadProfileApi {

    @GET("/profile/load/")
    Call<Profile> loadProfile();
}
