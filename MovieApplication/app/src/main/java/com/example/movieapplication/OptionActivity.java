package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.movieapplication.MainActivity.MY_URL;
import static com.example.movieapplication.MainActivity.IMG;

public class OptionActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView op_login, op_email;
    ImageView img;
    Button op_photo_btn;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //zaladowanie toolbara
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.settings_title);
        setSupportActionBar(toolbar);

        //instancja FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        initialize();

        load_data();

        //zmiana zdjecia profilowego
        op_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 99);
            }
        });

        //obsluga navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent log_out = new Intent(OptionActivity.this, MainActivity.class);
                Intent home = new Intent(OptionActivity.this, HomeActivity.class);
                Intent profile = new Intent(OptionActivity.this, ProfileActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //załadowanie danych z bazy danych
    public void load_data (){
        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        ProfileDetails profileDetails = new ProfileDetails(userID);
        profile_details(profileDetails);
        set_values();
    }

    //załadowanie emeila, loginu, headera i zdjecia z bazy danych
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
                Toast.makeText(getApplicationContext(), "załadowano header, email, login", Toast.LENGTH_LONG).show();
                User user = response.body();
                View nav_header = navigationView.getHeaderView(0);
                TextView header = nav_header.findViewById(R.id.nav_header);
                assert user != null;
                header.setText("Witaj " + user.get_login() + "!");
                op_login.setText(user.get_login());
                op_email.setText(user.get_email());
                if(IMG != null) {
                    View img_header = navigationView.getHeaderView(0);
                    ImageView header_img = img_header.findViewById(R.id.header_img);
                    header_img.setImageBitmap(IMG);
                }
                progressBar.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano headera, emaila, loginu", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    //zapisanie wybranego zdjecia w bazie danych
    private void put_storage(String img) {

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

        UserStorageApi userStorageApi= retrofit.create(UserStorageApi.class);

        Call<ResponseBody> call = userStorageApi.putStorage(img);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "zapisano img", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie zapisano img", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
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

    //obsługa onActivityResult dla wybranego zdjecia z galerii
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == RESULT_OK && null != data) {
            progressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            img.setImageURI(null);
            img.setBackground(null);
            img.setImageURI(selectedImage);

            InputStream imageStream = null;
            try {
                assert selectedImage != null;
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap imgBitmap = BitmapFactory.decodeStream(imageStream);
            IMG = imgBitmap;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);

            String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
            ProfileDetails profileDetails = new ProfileDetails(userID);
            profile_details(profileDetails);
            put_storage(encImage);

        }


    }

    //inicjalizacja zmiennych
    public void initialize () {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        View nav_header_title = navigationView.getHeaderView(0);
        TextView header_title = nav_header_title.findViewById(R.id.nav_header_title);
        header_title.setText(R.string.app_name);

        linearLayout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.op_progress_bar);
        loading = findViewById(R.id.op_loading);
        op_login = findViewById(R.id.opt_login);
        op_email = findViewById(R.id.opt_email);

        View img_header = navigationView.getHeaderView(0);
        img = img_header.findViewById(R.id.header_img);

        op_photo_btn = findViewById(R.id.op_photo_btn);

        Menu nav_Menu = navigationView.getMenu();

        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);
    }

}
