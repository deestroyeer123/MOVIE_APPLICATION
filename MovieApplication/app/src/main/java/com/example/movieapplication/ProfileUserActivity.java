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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.movieapplication.MainActivity.MY_URL;
import static com.example.movieapplication.MainActivity.IMG;
import static com.example.movieapplication.HomeActivity.bitmapsImages;

public class ProfileUserActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageView profile_user_img;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    TextView loading;
    TextView name, sex, age, movie1, movie2, movie3, elem1, elem2, elem3, place, country1, country2, country3,
            actor1, actor2, actor3, director1, director2, director3, oscar, years1, years2, years3, food1, food2, food3, group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        Intent showProfile = getIntent();
        ImageProfile selected_profile = (ImageProfile) showProfile.getSerializableExtra("selectedProfile");
        Intent getPosition = getIntent();
        int position = getPosition.getIntExtra("position", 0);

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

        View nav_header_title = navigationView.getHeaderView(0);
        TextView header_title = nav_header_title.findViewById(R.id.nav_header_title);
        header_title.setText(R.string.app_name);

        profile_user_img = findViewById(R.id.profile_user_img);
        name = findViewById(R.id.profile_user_name);
        age = findViewById(R.id.profile_user_age);
        sex = findViewById(R.id.profile_user_sex);
        movie1 = findViewById(R.id.profile_user_movie1);
        movie2 = findViewById(R.id.profile_user_movie2);
        movie3 = findViewById(R.id.profile_user_movie3);
        elem1 = findViewById(R.id.profile_user_elem1);
        elem2 = findViewById(R.id.profile_user_elem2);
        elem3 = findViewById(R.id.profile_user_elem3);
        place = findViewById(R.id.profile_user_place);
        country1 = findViewById(R.id.profile_user_country1);
        country2 = findViewById(R.id.profile_user_country2);
        country3 = findViewById(R.id.profile_user_country3);
        actor1 = findViewById(R.id.profile_user_actor1);
        actor2 = findViewById(R.id.profile_user_actor2);
        actor3 = findViewById(R.id.profile_user_actor3);
        director1 = findViewById(R.id.profile_user_director1);
        director2 = findViewById(R.id.profile_user_director2);
        director3 = findViewById(R.id.profile_user_director3);
        oscar = findViewById(R.id.profile_user_oscar);
        years1 = findViewById(R.id.profile_user_years1);
        years2 = findViewById(R.id.profile_user_years2);
        years3 = findViewById(R.id.profile_user_years3);
        food1 = findViewById(R.id.profile_user_food1);
        food2 = findViewById(R.id.profile_user_food2);
        food3 = findViewById(R.id.profile_user_food3);
        group = findViewById(R.id.profile_user_group);
        relativeLayout = findViewById(R.id.profile_user_layout);
        progressBar = findViewById(R.id.profile_user_progress_bar);
        loading = findViewById(R.id.profile_user_loading);

        load_data();
        set_profile(selected_profile, position);
        //profile_user_img.setImageBitmap(IMG);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent log_out = new Intent(ProfileUserActivity.this, MainActivity.class);
                Intent home = new Intent(ProfileUserActivity.this, HomeActivity.class);
                Intent profile = new Intent(ProfileUserActivity.this, ProfileActivity.class);
                Intent settings = new Intent(ProfileUserActivity.this, OptionActivity.class);
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
                }

                return false;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void set_profile(ImageProfile profile, int position){
        if(bitmapsImages.get(position) != null) {
            profile_user_img.setImageBitmap(bitmapsImages.get(position));
        }
        name.setText(profile.get_name());
        sex.setText(profile.get_sex());
        age.setText("Wiek: " + profile.get_age());
        movie1.setText(profile.get_movie1());
        movie2.setText(profile.get_movie2());
        movie3.setText(profile.get_movie3());
        elem1.setText(profile.get_elem1());
        elem2.setText(profile.get_elem2());
        elem3.setText(profile.get_elem3());
        place.setText(profile.get_place());
        country1.setText(profile.get_country1());
        country2.setText(profile.get_country2());
        country3.setText(profile.get_country3());
        actor1.setText(profile.get_actor1());
        actor2.setText(profile.get_actor2());
        actor3.setText(profile.get_actor3());
        director1.setText(profile.get_director1());
        director2.setText(profile.get_director2());
        director3.setText(profile.get_director3());
        if(profile.get_oscar()) oscar.setText("Tak");
        else oscar.setText("Nie");
        years1.setText(profile.get_years1());
        years2.setText(profile.get_years2());
        years3.setText(profile.get_years3());
        food1.setText(profile.get_food1());
        food2.setText(profile.get_food2());
        food3.setText(profile.get_food3());
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
                if(IMG != null) {
                    View img_header = navigationView.getHeaderView(0);
                    ImageView header_img = img_header.findViewById(R.id.header_img);
                    header_img.setImageBitmap(IMG);
                }

                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
}
