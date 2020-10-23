package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewUserApi {

    String MY_URL = "http://192.168.43.139:8000";

    @POST("/user/")
    Call<User> addUser(@Body User user);
}
