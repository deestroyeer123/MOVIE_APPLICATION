package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.movieapplication.RegisterActivity.newUser;
import static com.example.movieapplication.RegisterActivity.users;


public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView title_movies, title_elements, title_countries, title_years, title_foods, loading;
    RadioGroup sex, place, oscar, group;
    Spinner spin_actors, spin_actors2, spin_actors3, spin_directors, spin_directors2, spin_directors3;
    EditText pr_name, pr_age;
    RadioButton man, woman, oscars_true, oscars_false, group_true, group_false, home, cinema, outside;
    CheckBox action_film, comedy, drama, fantasy, horror, disaster_film, adv_film, scifi, thriller,
    western, war_film, romantic, animated, biographical, documentary;
    CheckBox music, acting, scenography, actors, director, plot;
    CheckBox argentina, australia, belgium, france, greece, spain, india, japan, canada, mexico,
    germany, poland, usa, uk, italy;
    CheckBox r1990, r2000, r2010, r2015, r2019, r2020;
    CheckBox popcorn, nachos, chips, sticks, cookies, nothing;
    Button save, edit;
    ScrollView scrollView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.profile_title);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        initialize();

        final CheckBox[] movie_types = {action_film, comedy, drama, fantasy, horror, disaster_film, adv_film, scifi, thriller,
                western, war_film, romantic, animated, biographical, documentary};
        final CheckBox[] movie_elements = {music, acting, scenography, actors, director, plot};
        final CheckBox[] country_production = {argentina, australia, belgium, france, greece, spain, india, japan, canada, mexico,
                germany, poland, usa, uk, italy};
        final CheckBox[] year_production = {r1990, r2000, r2010, r2015, r2019, r2020};
        final CheckBox[] food = {popcorn, nachos, chips, sticks, cookies, nothing};
        final RadioGroup[] radioGroups = {sex, place, oscar, group};
        final RadioButton[] radioButtons = {man, home, oscars_true, group_true};

        load_spinners();
        set_profile_values(movie_types, movie_elements, country_production, year_production, food);

        max3_all_tables(movie_types, title_movies);
        max3_all_tables(movie_elements, title_elements);
        max3_all_tables(country_production, title_countries);
        max3_all_tables(year_production, title_years);
        max3_all_tables(food, title_foods);

        radio_group_changes(radioGroups, radioButtons);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_enabled(movie_types, movie_elements, country_production, year_production, food);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(required_fields_ok(movie_types, movie_elements, country_production, year_production, food)) {
                    List<String> movies;
                    List<String> elements;
                    List<String> countries;
                    List<String> years;
                    List<String> foods;
                    String name = pr_name.getText().toString();
                    int age = Integer.parseInt(pr_age.getText().toString());
                    String sel_sex, sel_place, sel_group, actor1, actor2, actor3, director1, director2, director3;
                    boolean sel_oscar = false;
                    RadioButton selected_sex = findViewById(sex.getCheckedRadioButtonId());
                    RadioButton selected_place = findViewById(place.getCheckedRadioButtonId());
                    RadioButton selected_oscar = findViewById(oscar.getCheckedRadioButtonId());
                    if (selected_oscar.equals(oscars_true)) sel_oscar = true;
                    RadioButton selected_group = findViewById(group.getCheckedRadioButtonId());
                    sel_sex = selected_sex.getText().toString();
                    sel_place = selected_place.getText().toString();
                    sel_group = selected_group.getText().toString();

                    actor1 = spin_actors.getSelectedItem().toString();
                    actor2 = spin_actors2.getSelectedItem().toString();
                    actor3 = spin_actors3.getSelectedItem().toString();
                    director1 = spin_directors.getSelectedItem().toString();
                    director2 = spin_directors2.getSelectedItem().toString();
                    director3 = spin_directors3.getSelectedItem().toString();

                    movies = get_selected(movie_types);
                    elements = get_selected(movie_elements);
                    countries = get_selected(country_production);
                    years = get_selected(year_production);
                    foods = get_selected(food);

                    String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                    Profile profile = new Profile(name, age, sel_sex, movies.get(0), movies.get(1), movies.get(2), elements.get(0),
                            elements.get(1), elements.get(2), sel_place, countries.get(0), countries.get(1), countries.get(2),
                            actor1, actor2, actor3, director1, director2, director3, sel_oscar, years.get(0), years.get(1),
                            years.get(2), foods.get(0), foods.get(1), foods.get(2), sel_group);
                    ProfileDetails profileDetails = new ProfileDetails(userID);
                    profile_details(profileDetails);
                    create_profile(profile);
                }

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent settings = new Intent(ProfileActivity.this, OptionActivity.class);
                Intent log_out = new Intent(ProfileActivity.this, MainActivity.class);
                Intent home = new Intent(ProfileActivity.this, HomeActivity.class);
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(home);
                        return true;
                    case R.id.nav_profile:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
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

    @Override
    public void onBackPressed(){
        if(!newUser) super.onBackPressed();
    }

    public void create_profile(Profile profile) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewProfileApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewProfileApi newProfileApi = retrofit.create(NewProfileApi.class);

        Call<Profile> call = newProfileApi.addProfile(profile);

        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "utworzono/zaktualizowano profil", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie udało się utworzyć/zaktualizować profilu", Toast.LENGTH_LONG).show();
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

    public void load_profile(final CheckBox[] movie_types, final CheckBox[] movie_elements, final CheckBox[] countries,
                             final CheckBox[] years, final CheckBox[] foods) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LoadProfileApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoadProfileApi loadProfileApi = retrofit.create(LoadProfileApi.class);

        Call<Profile> call = loadProfileApi.loadProfile();

        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "załadowano dane", Toast.LENGTH_LONG).show();

                Profile profile = response.body();

                if(profile != null) {
                    pr_name.setText(profile.get_name());
                    pr_age.setText(String.valueOf(profile.get_age()));
                    if (man.getText().toString().equals(profile.get_sex())) man.setChecked(true);
                    else woman.setChecked(true);
                    checked_checkboxes(movie_types, profile.get_movie1());
                    checked_checkboxes(movie_types, profile.get_movie2());
                    checked_checkboxes(movie_types, profile.get_movie3());
                    checked_checkboxes(movie_elements, profile.get_elem1());
                    checked_checkboxes(movie_elements, profile.get_elem2());
                    checked_checkboxes(movie_elements, profile.get_elem3());
                    if (home.getText().toString().equals(profile.get_place()))
                        home.setChecked(true);
                    else if (cinema.getText().toString().equals(profile.get_place()))
                        cinema.setChecked(true);
                    else outside.setChecked(true);
                    checked_checkboxes(countries, profile.get_country1());
                    checked_checkboxes(countries, profile.get_country2());
                    checked_checkboxes(countries, profile.get_country3());
                    selected_actors(spin_actors, profile.get_actor1());
                    selected_actors(spin_actors2, profile.get_actor2());
                    selected_actors(spin_actors3, profile.get_actor3());
                    selected_directors(spin_directors, profile.get_director1());
                    selected_directors(spin_directors2, profile.get_director2());
                    selected_directors(spin_directors3, profile.get_director3());
                    if (profile.get_oscar()) oscars_true.setChecked(true);
                    else oscars_false.setChecked(true);
                    checked_checkboxes(years, profile.get_years1());
                    checked_checkboxes(years, profile.get_years2());
                    checked_checkboxes(years, profile.get_years3());
                    checked_checkboxes(foods, profile.get_food1());
                    checked_checkboxes(foods, profile.get_food2());
                    checked_checkboxes(foods, profile.get_food3());
                    if (group_true.getText().toString().equals(profile.get_group()))
                        group_true.setChecked(true);
                    else group_false.setChecked(true);
                    set_disenabled(movie_types, movie_elements, countries, years, foods);
                    newUser = false;
                }

                progressBar.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie ma profilu", Toast.LENGTH_LONG).show();

                progressBar.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void selected_actors(Spinner spinner, String val){
        List<String> actors = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Actors)));
        Collections.sort(actors);
        for(int i = 0; i < actors.size(); i++){
            if(actors.get(i).equals(val)) {
                spinner.setSelection(i + 1);
                break;
            }
        }
    }

    public void selected_directors(Spinner spinner, String val){
        List<String> directors = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Directors)));
        Collections.sort(directors);
        for(int i = 0; i < directors.size(); i++){
            if(directors.get(i).equals(val)) {
                spinner.setSelection(i + 1);
                break;
            }
        }
    }

    public void checked_checkboxes(CheckBox[] checkBoxes, String val){
        for (CheckBox checkBox : checkBoxes) {
            if(val.equals(checkBox.getText().toString())) {
                checkBox.setChecked(true);
                break;
            }
        }
    }

    public void set_profile_values(final CheckBox[] movie_types, final CheckBox[] movie_elements, final CheckBox[] countries,
                                   final CheckBox[] years, final CheckBox[] foods){
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        ProfileDetails profileDetails = new ProfileDetails(userID);
        profile_details(profileDetails);
        load_profile(movie_types, movie_elements, countries, years, foods);
        set_values();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie załadowano headera", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void max3_selected (CheckBox[] tab, TextView textView){
        int j = 0;
        for (CheckBox checkBox : tab) if (checkBox.isChecked()) j++;
        if(j >= 3){
            for (CheckBox checkBox : tab) {
                if (checkBox.isChecked()) continue;
                checkBox.setEnabled(false);
                textView.setError(null);
            }
        }
        else{
            for (CheckBox checkBox : tab) {
                if (checkBox.isChecked()) continue;
                checkBox.setEnabled(true);
            }
        }
    }

    public void max3_all_tables (final CheckBox[] tab, final TextView textView){
        for (CheckBox checkBox : tab) {
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    max3_selected(tab, textView);
                }
            });
        }
    }


    public List<String> get_selected(CheckBox[] tab) {
        List<String> selected = new ArrayList<>();
        for (CheckBox checkBox : tab) {
            if(checkBox.isChecked()) selected.add(checkBox.getText().toString());
        }
        return selected;
    }

    public boolean check_selected (CheckBox[] tab){
        int j = 0;
        for(CheckBox checkBox : tab){
            if(checkBox.isChecked()) j++;
        }
        return j == 3;
    }

    public boolean required_fields_ok (CheckBox[] movies, CheckBox[] elements, CheckBox[] countries, CheckBox[] years, CheckBox[] foods)
    {
        int j = 0;
        if(TextUtils.isEmpty(pr_name.getText())) {
            pr_name.setError("Wpisz imię");
        }
        else j++;
        if(TextUtils.isEmpty(pr_age.getText())) {
            pr_age.setError("Wpisz wiek");
        }
        else j++;
        if(sex.getCheckedRadioButtonId() == -1) {
            man.setError("Podaj płeć");
        }
        else j++;
        if(!check_selected(movies)) {
            title_movies.setError("Wybierz 3 filmy");
        }
        else j++;
        if(!check_selected(elements)) {
            title_elements.setError("Wybierz 3 elementy");
        }
        else j++;
        if(place.getCheckedRadioButtonId() == -1) {
            home.setError("Wybierz miejsce");
        }
        else j++;
        if(!check_selected(countries)) {
            title_countries.setError("Wybierz 3 kraje");
        }
        else j++;
        if(spin_actors.getSelectedItem().toString().equals(getString(R.string.profile_spin_actors)))
            ((TextView)spin_actors.getSelectedView()).setError("Wybierz aktora");
        else j++;
        if(spin_actors2.getSelectedItem().toString().equals(getString(R.string.profile_spin_actors)))
            ((TextView)spin_actors2.getSelectedView()).setError("Wybierz aktora");
        else j++;
        if(spin_actors3.getSelectedItem().toString().equals(getString(R.string.profile_spin_actors)))
            ((TextView)spin_actors3.getSelectedView()).setError("Wybierz aktora");
        else j++;
        if(spin_directors.getSelectedItem().toString().equals(getString(R.string.profile_spin_directors)))
            ((TextView)spin_directors.getSelectedView()).setError("Wybierz reżysera");
        else j++;
        if(spin_directors2.getSelectedItem().toString().equals(getString(R.string.profile_spin_directors)))
            ((TextView)spin_directors2.getSelectedView()).setError("Wybierz reżysera");
        else j++;
        if(spin_directors3.getSelectedItem().toString().equals(getString(R.string.profile_spin_directors)))
            ((TextView)spin_directors3.getSelectedView()).setError("Wybierz reżysera");
        else j++;
        if(oscar.getCheckedRadioButtonId() == -1) {
            oscars_true.setError("Zaznacz tak lub nie");
        }
        else j++;
        if(!check_selected(years)) {
            title_years.setError("Wybierz 3 przedziały lat");
        }
        else j++;
        if(!check_selected(foods)) {
            title_foods.setError("Wybierz 3 przekąski");
        }
        else j++;
        if(group.getCheckedRadioButtonId() == -1) {
            group_true.setError("Wybierz grupę");
        }
        else j++;

        return j == 17;
    }

    public void load_spinners (){
        List<String> actors_sorted = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Actors)));
        Collections.sort(actors_sorted);
        actors_sorted.add(0, getString(R.string.profile_spin_actors));
        ArrayAdapter<String> actors_adapter = new ArrayAdapter<>(ProfileActivity.this, R.layout.custom_spinner, actors_sorted);
        actors_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spin_actors.setAdapter(actors_adapter);
        spin_actors2.setAdapter(actors_adapter);
        spin_actors3.setAdapter(actors_adapter);
        List<String> directors_sorted = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Directors)));
        Collections.sort(directors_sorted);
        directors_sorted.add(0, getString(R.string.profile_spin_directors));
        ArrayAdapter<String> directors_adapter = new ArrayAdapter<>(ProfileActivity.this, R.layout.custom_spinner, directors_sorted);
        directors_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spin_directors.setAdapter(directors_adapter);
        spin_directors2.setAdapter(directors_adapter);
        spin_directors3.setAdapter(directors_adapter);
    }

    public void radio_group_changes (RadioGroup[] radioGroups, final RadioButton[] radioButtons){
        for (int i = 0; i < radioGroups.length; i++) {
            final int finalI = i;
            radioGroups[i].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    radioButtons[finalI].setError(null);
                }
            });
        }
    }

    public void checkboxes_state(CheckBox[] checkBoxes, boolean state){
        for (CheckBox checkBox : checkBoxes) checkBox.setEnabled(state);
    }

    public void set_disenabled(CheckBox[] movies, CheckBox[] elements, CheckBox[] countries, CheckBox[] years, CheckBox[] foods){
        pr_name.setEnabled(false);
        pr_age.setEnabled(false);
        man.setEnabled(false);
        woman.setEnabled(false);
        checkboxes_state(movies, false);
        checkboxes_state(elements, false);
        home.setEnabled(false);
        cinema.setEnabled(false);
        outside.setEnabled(false);
        checkboxes_state(countries, false);
        spin_actors.setEnabled(false);
        spin_actors2.setEnabled(false);
        spin_actors3.setEnabled(false);
        spin_directors.setEnabled(false);
        spin_directors2.setEnabled(false);
        spin_directors3.setEnabled(false);
        oscars_true.setEnabled(false);
        oscars_false.setEnabled(false);
        checkboxes_state(years, false);
        checkboxes_state(foods, false);
        group_true.setEnabled(false);
        group_false.setEnabled(false);
        save.setEnabled(false);
    }

    public void set_enabled(CheckBox[] movies, CheckBox[] elements, CheckBox[] countries, CheckBox[] years, CheckBox[] foods){
        pr_name.setEnabled(true);
        pr_age.setEnabled(true);
        man.setEnabled(true);
        woman.setEnabled(true);
        checkboxes_state(movies, true);
        checkboxes_state(elements, true);
        home.setEnabled(true);
        cinema.setEnabled(true);
        outside.setEnabled(true);
        checkboxes_state(countries, true);
        spin_actors.setEnabled(true);
        spin_actors2.setEnabled(true);
        spin_actors3.setEnabled(true);
        spin_directors.setEnabled(true);
        spin_directors2.setEnabled(true);
        spin_directors3.setEnabled(true);
        oscars_true.setEnabled(true);
        oscars_false.setEnabled(true);
        checkboxes_state(years, true);
        checkboxes_state(foods, true);
        group_true.setEnabled(true);
        group_false.setEnabled(true);
        save.setEnabled(true);
    }

   public void initialize(){
       drawerLayout = findViewById(R.id.drawer_layout);
       navigationView = findViewById(R.id.navigation_view);

       loading = findViewById(R.id.profile_loading);
       progressBar = findViewById(R.id.progress_bar);
       scrollView = findViewById(R.id.scroll_view);
       title_countries = findViewById(R.id.profile_title_country);
       title_elements = findViewById(R.id.profile_title_important);
       title_foods = findViewById(R.id.profile_title_food);
       title_movies = findViewById(R.id.profile_title_type);
       title_years = findViewById(R.id.profile_title_years);
       sex = findViewById(R.id.radio_group_sex);
       place = findViewById(R.id.radio_group_place);
       oscar = findViewById(R.id.radio_group_oscars);
       group = findViewById(R.id.radio_group_groups);
       save = findViewById(R.id.profile_save_btn);
       edit = findViewById(R.id.profile_edit_btn);
       spin_directors = findViewById(R.id.profile_spin_directors);
       spin_directors2 = findViewById(R.id.profile_spin_directors2);
       spin_directors3 = findViewById(R.id.profile_spin_directors3);
       spin_actors = findViewById(R.id.profile_spin_actors);
       spin_actors2 = findViewById(R.id.profile_spin_actors2);
       spin_actors3 = findViewById(R.id.profile_spin_actors3);
       pr_name = findViewById(R.id.profile_name);
       pr_age = findViewById(R.id.profile_age);
       man = findViewById(R.id.profile_man);
       woman = findViewById(R.id.profile_woman);
       oscars_true = findViewById(R.id.profile_oscars_true);
       oscars_false = findViewById(R.id.profile_oscars_false);
       group_true = findViewById(R.id.profile_group_more);
       group_false = findViewById(R.id.profile_group_less);
       action_film = findViewById(R.id.profile_action_film);
       comedy = findViewById(R.id.profile_comedy);
       drama = findViewById(R.id.profile_drama_film);
       fantasy = findViewById(R.id.profile_fantasy);
       horror = findViewById(R.id.profile_horror);
       disaster_film = findViewById(R.id.profile_disaster_film);
       adv_film = findViewById(R.id.profile_adventure_film);
       scifi = findViewById(R.id.profile_scifi);
       thriller = findViewById(R.id.profile_thriller);
       western = findViewById(R.id.profile_western);
       war_film = findViewById(R.id.profile_war_film);
       romantic = findViewById(R.id.profile_romantic);
       animated = findViewById(R.id.profile_animated_film);
       biographical = findViewById(R.id.profile_biographical_film);
       documentary = findViewById(R.id.profile_documentary);
       music = findViewById(R.id.profile_music);
       acting = findViewById(R.id.profile_acting);
       scenography = findViewById(R.id.profile_scenography);
       actors = findViewById(R.id.profile_actors);
       director = findViewById(R.id.profile_director);
       plot = findViewById(R.id.profile_plot);
       home = findViewById(R.id.profile_home);
       cinema = findViewById(R.id.profile_cinema);
       outside = findViewById(R.id.profile_outside);
       argentina = findViewById(R.id.profile_argentina);
       australia = findViewById(R.id.profile_australia);
       belgium = findViewById(R.id.profile_belgium);
       france = findViewById(R.id.profile_france);
       greece = findViewById(R.id.profile_greece);
       spain = findViewById(R.id.profile_spain);
       india = findViewById(R.id.profile_india);
       japan = findViewById(R.id.profile_japan);
       canada = findViewById(R.id.profile_canada);
       mexico = findViewById(R.id.profile_mexico);
       germany = findViewById(R.id.profile_germany);
       poland = findViewById(R.id.profile_poland);
       usa = findViewById(R.id.profile_usa);
       uk = findViewById(R.id.profile_uk);
       italy = findViewById(R.id.profile_italy);
       r1990 = findViewById(R.id.profile_less_than_1990);
       r2000 = findViewById(R.id.profile_1990_2000);
       r2010 = findViewById(R.id.profile_2000_2010);
       r2015 = findViewById(R.id.profile_2010_2015);
       r2019 = findViewById(R.id.profile_2015_2019);
       r2020 = findViewById(R.id.profile_more_than_2019);
       popcorn = findViewById(R.id.profile_popcorn);
       nachos = findViewById(R.id.profile_nachos);
       chips = findViewById(R.id.profile_chips);
       sticks = findViewById(R.id.profile_salty_sticks);
       cookies = findViewById(R.id.profile_cookies);
       nothing = findViewById(R.id.profile_food_nothing);
   }


}