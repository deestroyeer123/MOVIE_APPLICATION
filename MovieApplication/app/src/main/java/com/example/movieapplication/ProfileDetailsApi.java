package com.example.movieapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProfileDetailsApi {

    //zapytanie do serwera do przesłanie uid aktualnego uzytkownika
    @POST("/profile/details/")
    Call<ProfileDetails> sendDetails(@Body ProfileDetails profileDetails);
}
