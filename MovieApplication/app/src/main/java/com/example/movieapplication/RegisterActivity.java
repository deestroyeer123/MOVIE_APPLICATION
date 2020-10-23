package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    EditText reg_login, reg_email, reg_password, reg_password2;
    Button reg_btn;
    TextView tv_login, loading;
    ProgressBar progressBar;
    String ost_login, ost_email;

    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static String users = "users";
    public static boolean newUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.register_title);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_home).setVisible(false);
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_profile).setVisible(false);
        nav_Menu.findItem(R.id.nav_settings).setVisible(false);

        loading = findViewById(R.id.reg_loading);
        progressBar = findViewById(R.id.reg_progress_bar);
        tv_login = findViewById(R.id.tv_login);
        reg_login = findViewById(R.id.log_login);
        reg_password = findViewById(R.id.log_password);
        reg_password2 = findViewById(R.id.log_password_confirm);
        reg_email = findViewById(R.id.log_email);
        reg_btn = findViewById(R.id.log_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = reg_login.getText().toString();
                final String password = reg_password.getText().toString();
                String password2 = reg_password2.getText().toString();
                final String email = reg_email.getText().toString();
                if(required_fields_ok(login, email, password, password2)) {
                    progressBar.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    reg_password.setError("Zbyt słabe hasło! Hasło musi zawierać minimum 6 znaków!");
                                    reg_password.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    reg_email.setError("Niepoprawny format adresu mailowego!");
                                    reg_email.requestFocus();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    reg_email.setError("Istnieje już konto z podanym adresem mailowym!");
                                    reg_email.requestFocus();
                                } catch(Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "Niepowodzenie, spróbuj jeszcze raz", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                ost_login = login;
                                ost_email = email;
                                String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                ProfileDetails profileDetails = new ProfileDetails(userID);
                                profile_details(profileDetails);
                            }
                        }
                    });
                }


            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                switch (item.getItemId())
                {
                    case R.id.nav_login:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(login);
                        return true;
                    case R.id.nav_registration:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
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

    public boolean required_fields_ok (String login, String email, String password, String password2)
    {
        boolean log = false, em = false, pwd = false, pwd2 = false;
        EditText[] tab_edit = {reg_login, reg_email, reg_password, reg_password2};
        int j = 0;
        if(login.isEmpty()) {
            reg_login.setError("Wpisz login");
            log = true;
        }
        else j++;
        if(email.isEmpty()) {
            reg_email.setError("Wpisz email");
            em = true;
        }
        else j++;
        if(password.isEmpty()) {
            reg_password.setError("Wpisz hasło");
            pwd = true;
        }
        else j++;
        if(password2.isEmpty()) {
            reg_password2.setError("Potwierdź hasło");
            pwd2 = true;
        }
        else j++;
        Boolean[] empty = {log, em, pwd, pwd2};
        for(int i = 0; i < empty.length; i++){
            if(empty[i]) {
                tab_edit[i].requestFocus();
                break;
            }
        }
        return j == tab_edit.length;
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

                User user = new User(ost_login, ost_email);
                createNewUser(user);
                newUser = true;
                Toast.makeText(RegisterActivity.this, "Zarejestrowano", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
            }
            @Override
            public void onFailure(Call<ProfileDetails> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "nie udało się przesłać uid", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createNewUser(User user) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewUserApi.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewUserApi newUserApi = retrofit.create(NewUserApi.class);

        Call<User> call = newUserApi.addUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("good", "good");
                Toast.makeText(getApplicationContext(), "Zarejestrowano", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("fail", "fail");
                Toast.makeText(getApplicationContext(), "Nie udało się zarejestrować", Toast.LENGTH_LONG).show();
            }
        });
    }

}
