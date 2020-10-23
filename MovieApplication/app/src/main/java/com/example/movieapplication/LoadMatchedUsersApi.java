package com.example.movieapplication;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadMatchedUsersApi {

    String MY_URL = "http://192.168.43.139:8000";

    @GET("/matched/")
    Call<List<Profile>> loadMatchedUsers();
}
