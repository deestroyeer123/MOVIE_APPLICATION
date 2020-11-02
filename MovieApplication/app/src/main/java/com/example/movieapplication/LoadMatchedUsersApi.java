package com.example.movieapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadMatchedUsersApi {

    @GET("/matched/")
    Call<List<ImageProfile>> loadMatchedUsers();
}
