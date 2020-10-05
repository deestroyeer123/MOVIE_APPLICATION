package com.example.movieapplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ProfileApi {

    String MY_URL = "http://192.168.0.3:8000/" + "profile/";

    @PUT("{id}/edit/")
    Call<Profile> updateProfile(@Body Profile profileModel, @Path(value = "id", encoded = true) String id);

}

