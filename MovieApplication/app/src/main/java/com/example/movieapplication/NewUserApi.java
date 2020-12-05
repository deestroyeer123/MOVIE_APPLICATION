package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NewUserApi {

    //zapytanie do serwera o stworzenie nowego uzytkownika
    @POST("/user/")
    Call<User> addUser(@Body User user);
}




