package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewProfileApi {

    @POST("/profile/")
    Call<Profile> addProfile(@Body Profile profile);

}
