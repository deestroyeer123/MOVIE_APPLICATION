package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadUserApi {

    @GET("/user/details/")
    Call<User> loadUserDetails();
}
