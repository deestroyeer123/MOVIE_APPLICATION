package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


import static com.example.movieapplication.RegisterActivity.users;


public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    TextView title_movies, title_elements, title_countries, title_years, title_foods;
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
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //new ParseTask().execute();
        //update_profile();
        create_profile();

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.profile_title);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

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

        load_spinners();

        final CheckBox[] movie_types = {action_film, comedy, drama, fantasy, horror, disaster_film, adv_film, scifi, thriller,
                western, war_film, romantic, animated, biographical, documentary};
        final CheckBox[] movie_elements = {music, acting, scenography, actors, director, plot};
        final CheckBox[] country_production = {argentina, australia, belgium, france, greece, spain, india, japan, canada, mexico,
                germany, poland, usa, uk, italy};
        final CheckBox[] year_production = {r1990, r2000, r2010, r2015, r2019, r2020};
        final CheckBox[] food = {popcorn, nachos, chips, sticks, cookies, nothing};
        final RadioGroup[] radioGroups = {sex, place, oscar, group};
        final RadioButton[] radioButtons = {man, home, oscars_true, group_true};

        set_values();
        max3_all_tables(movie_types, title_movies);
        max3_all_tables(movie_elements, title_elements);
        max3_all_tables(country_production, title_countries);
        max3_all_tables(year_production, title_years);
        max3_all_tables(food, title_foods);

        radio_group_changes(radioGroups, radioButtons);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);

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

                    Profile profile = new Profile(name, age, sel_sex, movies.get(0), movies.get(1), movies.get(2), elements.get(0),
                            elements.get(1), elements.get(2), sel_place, countries.get(0), countries.get(1), countries.get(2),
                            actor1, actor2, actor3, director1, director2, director3, sel_oscar, years.get(0), years.get(1),
                            years.get(2), foods.get(0), foods.get(1), foods.get(2), sel_group);
                    update_profile();
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
                        startActivity(log_out);
                        return true;
                }

                return false;
            }
        });
    }

    private void update_profile ()  {

        //IPAddress ipAddress = new IPAddress();
        //String addr = ipAddress.get_ipaddress();
        //String MY_URL = "http://" + addr + ":8000/rest/";

        String id_profile = "1";
        Profile profile = new Profile("marek", 19, "facet", "film1", "film2", "film3", "muzyka",
                "cos", "cos2", "domek", "niemcy", "niemcy", "rosja",
                "actor1", "actor2", "actor3", "director1", "director2", "director3", true,
                "2000", "1900", "1990", "piwo", "flacha", "chipsy", "mała");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ProfileApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProfileApi profileApi= retrofit.create(ProfileApi.class);
        Call<Profile> call = profileApi.updateProfile(profile, id_profile);

        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void create_profile() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewProfileApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewProfileApi newProfileApi = retrofit.create(NewProfileApi.class);


        Profile profile = new Profile("marek", 19, "facet", "film1", "film2", "film3", "muzyka",
                "cos", "cos2", "domek", "niemcy", "niemcy", "rosja",
                "actor1", "actor2", "actor3", "director1", "director2", "director3", true,
                "2000", "1900", "1990", "piwo", "flacha", "chipsy", "mała");


        Call<RequestBody> call = newProfileApi.addProfile(profile);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try{
                String site_url_json = "192.168.0.8:8000/profile";
                URL url = new URL(site_url_json);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        protected void onPostExecute(String strJson){
            super.onPostExecute(strJson);

            try{
                JSONArray jsonArray = new JSONArray(strJson);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                String result_json_text = jsonObject.getString("text");
                Log.d("FOR LOG", result_json_text);

                //TextView textView = findViewById();
                //textView.setText(result_json_text);

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void set_values (){
        final String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = databaseReference.child(users).child(userID);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =  snapshot.getValue(User.class);
                assert user != null;
                View nav_header = navigationView.getHeaderView(0);
                TextView header = nav_header.findViewById(R.id.nav_header);
                header.setText(user.get_login());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
        databaseReference.removeEventListener(postListener);

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

    /*
    public String get_list_actors () throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://imdb8.p.rapidapi.com/actors/list-most-popular-celebs?currentCountry=US&purchaseCountry=US&homeCountry=US")
                .get()
                .addHeader("x-rapidapi-host", "imdb8.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "22786022a0msh6820e5e922653d4p125684jsn1c5bcc911f16")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

     */


}