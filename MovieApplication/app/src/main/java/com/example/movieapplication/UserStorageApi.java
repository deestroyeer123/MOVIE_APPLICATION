package com.example.movieapplication;

import android.app.DownloadManager;
import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserStorageApi {

    String MY_URL = "http://192.168.43.139:8000";

    //@POST("/user/storage/")
    //Call<Uri> putStorage(@Body Uri uri);

    @Multipart
    @POST("/user/storage/")
    Call<RequestBody>  putStorage(@Part MultipartBody.Part file);

}
