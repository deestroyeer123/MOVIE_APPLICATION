package com.example.movieapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface InitializeBaseApi {

    //zapytanie do serwera o baze wyborów
    @GET("/profile/base/")
    Call<ResponseBody> initialize();
}
