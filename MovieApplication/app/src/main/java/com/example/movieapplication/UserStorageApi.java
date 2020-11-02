package com.example.movieapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface UserStorageApi {

    @POST("/user/storage/")
    Call<ResponseBody> putStorage(@Body String img);

}
