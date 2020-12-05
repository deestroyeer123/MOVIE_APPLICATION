package com.example.movieapplication;


import retrofit2.Call;
import retrofit2.http.GET;

public interface LoadProfileApi {

    //zapytanie do serwera o załadowanie danych profilu aktualnie zalogowanego uzytkownika
    @GET("/profile/load/")
    Call<Profile> loadProfile();
}



