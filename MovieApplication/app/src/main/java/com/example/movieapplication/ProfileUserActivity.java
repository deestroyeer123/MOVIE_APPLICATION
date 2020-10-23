package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ProfileUserActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    TextView loading;
    TextView name, sex, age, movies, elements, place, countries, actors, directors, oscar, years, foods, group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        Intent showProfile = getIntent();
        Profile selected_profile = (Profile)showProfile.getSerializableExtra("selectedProfile");

        toolbar = findViewById(R.id.app_bar);
        assert selected_profile != null;
        toolbar.setTitle(selected_profile.get_name());
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        name = findViewById(R.id.profile_user_name);
        age = findViewById(R.id.profile_user_age);
        sex = findViewById(R.id.profile_user_sex);
        movies = findViewById(R.id.profile_user_movies);
        elements = findViewById(R.id.profile_user_elem);
        place = findViewById(R.id.profile_user_place);
        countries = findViewById(R.id.profile_user_countries);
        actors = findViewById(R.id.profile_user_actors);
        directors = findViewById(R.id.profile_user_directors);
        oscar = findViewById(R.id.profile_user_oscar);
        years = findViewById(R.id.profile_user_years);
        foods = findViewById(R.id.profile_user_foods);
        group = findViewById(R.id.profile_user_group);
        relativeLayout = findViewById(R.id.profile_user_layout);
        progressBar = findViewById(R.id.profile_user_progress_bar);
        loading = findViewById(R.id.profile_user_loading);

        load_data();
        set_profile(selected_profile);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent log_out = new Intent(ProfileUserActivity.this, MainActivity.class);
                Intent home = new Intent(ProfileUserActivity.this, HomeActivity.class);
                Intent profile = new Intent(ProfileUserActivity.this, ProfileActivity.class);
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
                }

                return false;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void set_profile(Profile profile){
        name.setText(profile.get_name());
        sex.setText(profile.get_sex());
        age.setText("Wiek: " + profile.get_age());
        movies.setText(profile.get_movie1() + ", " + profile.get_movie2() + ", " + profile.get_movie3());
        elements.setText(profile.get_elem1() + ", " + profile.get_elem2() + ", " + profile.get_elem3());
        place.setText(profile.get_place());
        countries.setText(profile.get_country1() + ", " + profile.get_country2() + ", " + profile.get_country3());
        actors.setText(profile.get_actor1() + ", " + profile.get_actor2() + ", " + profile.get_actor3());
        directors.setText(profile.get_director1() + ", " + profile.get_director2() + ", " + profile.get_director3());
        if(profile.get_oscar()) oscar.setText("Tak");
        else oscar.setText("Nie");
        years.setText(profile.get_years1() + ", " + profile.get_years2() + ", " + profile.get_years3());
        foods.setText(profile.get_food1() + ", " + profile.get_food2() + ", " + profile.get_food3());
        group.setText(profile.get_group());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void load_data (){
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        ProfileDetails profileDetails = new ProfileDetails(userID);
        profile_details(profileDetails);
        set_values();
    }

    public void set_values (){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoadUserApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoadUserApi loadUserApi = retrofit.create(LoadUserApi.class);

        Call<User> call = loadUserApi.loadUserDetails();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "załadowano header", Toast.LENGTH_LONG).show();

                User user = response.body();
                View nav_header = navigationView.getHeaderView(0);
                TextView header = nav_header.findViewById(R.id.nav_header);
                assert user != null;
                header.setText(user.get_login());

                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano headera", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void profile_details(ProfileDetails profileDetails) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ProfileDetailsApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProfileDetailsApi profileDetailsApi = retrofit.create(ProfileDetailsApi.class);

        Call<ProfileDetails> call = profileDetailsApi.sendDetails(profileDetails);

        call.enqueue(new Callback<ProfileDetails>() {
            @Override
            public void onResponse(Call<ProfileDetails> call, Response<ProfileDetails> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "przesłano uid", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie udało się przesłać uid", Toast.LENGTH_LONG).show();
            }
        });
    }
}
