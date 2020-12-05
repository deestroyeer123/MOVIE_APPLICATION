package com.example.movieapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadMatchedUsersApi {

    //zapytanie do serwera o liste profili osob dopasowanych do uzytkownika
    @GET("/matched/")
    Call<List<ImageProfile>> loadMatchedUsers();
}
