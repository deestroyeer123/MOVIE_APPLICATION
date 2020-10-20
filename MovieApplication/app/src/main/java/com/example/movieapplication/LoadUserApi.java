package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadUserApi {

    String MY_URL = "http://192.168.0.3:8000";

    @GET("/user/details/")
    Call<User> loadUserDetails();
}
