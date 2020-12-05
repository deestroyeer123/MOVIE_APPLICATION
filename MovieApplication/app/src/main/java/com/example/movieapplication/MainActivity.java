package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ProgressBar progressBar;
    TextView loading;
    ConstraintLayout constraintLayout;

    ImageView logo, text;
    LottieAnimationView lottieAnimationView;

    public static String MY_URL = "http://192.168.0.5:8000"; //adres ip na którym odpalony serwer
    public static Bitmap IMG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //załadowanie toolbara
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //instancja FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        initialize();

        //obsługa navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent registration = new Intent(MainActivity.this, RegisterActivity.class);
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                Intent settings = new Intent(MainActivity.this, OptionActivity.class);
                Intent log_out = new Intent(MainActivity.this, MainActivity.class);
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(home);
                        return true;
                    case R.id.nav_profile:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(profile);
                        return true;
                    case R.id.nav_settings:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(settings);
                        return true;
                    case R.id.nav_logout:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();
                        log_out.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        log_out.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        log_out.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(log_out);
                        return true;
                    case R.id.nav_login:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(login);
                        return true;
                    case R.id.nav_registration:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(registration);
                        return true;
                }

                return false;
            }
        });

    }

    //ładowanie danych z bazy danych
    public void load_data (){
        progressBar.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        ProfileDetails profileDetails = new ProfileDetails(userID);
        profile_details(profileDetails);
        set_values();
        initialize_base();
        load_img();
    }

    //załadowanie headera z bazy danych
    public void set_values (){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoadUserApi loadUserApi = retrofit.create(LoadUserApi.class);

        Call<User> call = loadUserApi.loadUserDetails();

        call.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "załadowano header", Toast.LENGTH_LONG).show();

                User user = response.body();
                View nav_header = navigationView.getHeaderView(0);
                TextView header = nav_header.findViewById(R.id.nav_header);
                assert user != null;
                header.setText("Witaj " + user.get_login() + "!");

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano headera", Toast.LENGTH_LONG).show();
            }
        });

    }

    //załadowanie bazy wyborów z bazy danych
    public void initialize_base() {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InitializeBaseApi initializeBaseApi = retrofit.create(InitializeBaseApi.class);

        Call<ResponseBody> call = initializeBaseApi.initialize();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "zainicjalizowano bazę wyborów", Toast.LENGTH_LONG).show();

            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie udało się zainicjalizować bazy wyborów", Toast.LENGTH_LONG).show();

            }
        });

    }

    //wysyłanie uid uzytkownika do backendu
    public void profile_details(ProfileDetails profileDetails) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProfileDetailsApi profileDetailsApi = retrofit.create(ProfileDetailsApi.class);

        Call<ProfileDetails> call = profileDetailsApi.sendDetails(profileDetails);

        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(@NonNull Call<ProfileDetails> call, @NonNull Response<ProfileDetails> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "przesłano uid", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(@NonNull Call<ProfileDetails> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie udało się przesłać uid", Toast.LENGTH_LONG).show();
            }
        });
    }

    //załadowanie zdjęcia profilowego z bazy danych
    public void load_img (){

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoadImgApi loadImgApi = retrofit.create(LoadImgApi.class);

        Call<String> call = loadImgApi.loadImg();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "załadowano img z bazy", Toast.LENGTH_LONG).show();

                String encodedImage = response.body();
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                View img_header = navigationView.getHeaderView(0);
                ImageView header_img = img_header.findViewById(R.id.header_img);
                header_img.setImageBitmap(decodedByte);
                IMG = decodedByte;

                progressBar.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie zaladowano img z bazy", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //inicjalizacja zmiennych
    public void initialize () {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View nav_header_title = navigationView.getHeaderView(0);
        TextView header_title = nav_header_title.findViewById(R.id.nav_header_title);
        header_title.setText(R.string.app_name);

        progressBar = findViewById(R.id.main_progress_bar);
        loading = findViewById(R.id.main_loading);
        constraintLayout = findViewById(R.id.main_layout);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Menu nav_Menu = navigationView.getMenu();
        if(firebaseUser != null) {
            load_data();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_registration).setVisible(false);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_settings).setVisible(false);
        }

        logo = findViewById(R.id.logo);
        text = findViewById(R.id.text);
        lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
    }

}
