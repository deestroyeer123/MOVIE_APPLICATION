package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadUserApi {

    //zapytanie do serwera o email i login aktualnie zalogowanego uzytkownika
    @GET("/user/details/")
    Call<User> loadUserDetails();
}


