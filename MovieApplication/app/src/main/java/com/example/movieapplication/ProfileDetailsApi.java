package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProfileDetailsApi {

    String MY_URL = "http://192.168.43.139:8000";

    @POST("/profile/details/")
    Call<ProfileDetails> sendDetails(@Body ProfileDetails profileDetails);
}
