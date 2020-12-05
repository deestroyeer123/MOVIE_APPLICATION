package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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

import static com.example.movieapplication.MainActivity.MY_URL;
import static com.example.movieapplication.MainActivity.IMG;
import static com.example.movieapplication.LoginActivity.already_logged;

import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.res.Resources;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    ListView item_list;
    CustomAdapter adapter;
    public HomeActivity CustomListView = null;
    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<>();

    FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView loading;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    CardView cardView;

    List<ImageProfile> profileList = new ArrayList<>(); //profile dla dopasowanych osób
    public static List<Bitmap> bitmapsImages = new ArrayList<>(); //bitmapy dla dopasowanych osób

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //załadowanie toolbara
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.home_title);
        setSupportActionBar(toolbar);

        //instancja FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        show_navigation_btn(false);

        initialize();

        load_data();

        CustomListView = this;

        //obsługa navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent settings = new Intent(HomeActivity.this, OptionActivity.class);
                Intent log_out = new Intent(HomeActivity.this, MainActivity.class);
                Intent profile = new Intent(HomeActivity.this, ProfileActivity.class);

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
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

    //podgląd wybranego profilu
    public void onItemClick(int mPosition) {
        ImageProfile selected_profile = profileList.get(mPosition); //wybrany profil do podglądu
        Intent sendProfile = new Intent(HomeActivity.this, ProfileUserActivity.class);
        sendProfile.putExtra("selectedProfile", selected_profile); //przesłanie wybranego profilu do aktywnosci podgladu
        sendProfile.putExtra("position", mPosition);  //wyslanie pozycji do aktywnosci podgladu w celu zaladowania odpowiedniego img
        startActivity(sendProfile);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //ładowanie danych z bazy danych
    public void load_data (){
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        ProfileDetails profileDetails = new ProfileDetails(userID);
        profile_details(profileDetails);
        set_values();
        load_matched_users();
    }

    //pokazanie lub ukrycie navigation btn'a (pokazanie dopiero po załadowaniu profili)
    public void show_navigation_btn(boolean state){
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(state);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    //załadowanie zdjecia i headera z bazy danych
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
                if(!already_logged) {
                    if (IMG != null) {
                        View img_header = navigationView.getHeaderView(0);
                        ImageView header_img = img_header.findViewById(R.id.header_img);
                        header_img.setImageBitmap(IMG);
                    }
                }
                else {
                    load_img();
                    already_logged = false;
                }

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano headera", Toast.LENGTH_LONG).show();
            }
        });

    }

    //zwrocenie bitmapy dla zasobu Drawable
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            assert drawable != null;
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        assert drawable != null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    //załadowanie listy osób wybranych dla użytkownika
    public void load_matched_users (){

        //dłuższy czas oczekiwania na response
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(1200, TimeUnit.SECONDS)
                .readTimeout(1200, TimeUnit.SECONDS)
                .writeTimeout(1200, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MY_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoadMatchedUsersApi loadMatchedUsersApi = retrofit.create(LoadMatchedUsersApi.class);

        Call<List<ImageProfile>> call = loadMatchedUsersApi.loadMatchedUsers();

        call.enqueue(new Callback<List<ImageProfile>>() {
            @Override
            public void onResponse(@NonNull Call<List<ImageProfile>> call, @NonNull Response<List<ImageProfile>> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "załadowano liste", Toast.LENGTH_LONG).show();

                List<ImageProfile> imageProfileList = response.body();
                assert imageProfileList != null;

                List<Bitmap> bitmaps = new ArrayList<>();

                //przechowywanie bitmap wybranych osob w osobnej liscie bitmaps nie przesyłanej do aktywnosci podgladu
                //profile wybranych osob bez zdjec (bo za duzy rozmiar)
                for(ImageProfile imageProfile : imageProfileList){
                    if(imageProfile.get_img() != null) {
                        byte[] decodedString = Base64.decode(imageProfile.get_img(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        bitmaps.add(decodedByte);
                    }
                    else{
                        bitmaps.add(null);
                    }
                    imageProfile.set_img(null);
                }

                bitmapsImages = bitmaps; //same bitmapy
                profileList = imageProfileList; //profile bez zdjec

                //ładowanie danych do listView
                for (int i = 0; i < imageProfileList.size(); i++) {

                    final ListModel sched = new ListModel();

                    sched.set_name(imageProfileList.get(i).get_name());
                    sched.set_age("Wiek: " + imageProfileList.get(i).get_age());
                    if(bitmaps.get(i) != null) sched.set_img(bitmaps.get(i));
                    else sched.set_img(getBitmapFromVectorDrawable(getApplicationContext(), R.drawable.ic_user));

                    CustomListViewValuesArr.add(sched);
                }

                Resources res = getResources();

                adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                item_list.setAdapter(adapter);

                show_navigation_btn(true); //pokazanie bavgation btn'a po załadowaniu listView
                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(@NonNull Call<List<ImageProfile>> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano listy", Toast.LENGTH_LONG).show();

                show_navigation_btn(true);
                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);


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
                relativeLayout.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie zaladowano img z bazy", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
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

    //inicjalizacja zmiennych
    public void initialize (){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        cardView = findViewById(R.id.card_view);
        progressBar = findViewById(R.id.home_progress_bar);
        relativeLayout = findViewById(R.id.home_layout);
        loading = findViewById(R.id.home_loading);
        item_list = findViewById(R.id.home_list);

        View nav_header_title = navigationView.getHeaderView(0);
        TextView header_title = nav_header_title.findViewById(R.id.nav_header_title);
        header_title.setText(R.string.app_name);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);
    }

}
