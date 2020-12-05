package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    EditText log_email, log_password;
    Button log_btn;
    TextView tv_register, loading;
    ProgressBar progressBar;

    public static boolean already_logged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //załadowanie toolbara
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.login_title);
        setSupportActionBar(toolbar);

        //instancja FirebaseAuthentication
        firebaseAuth = FirebaseAuth.getInstance();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        initialize();

        //zalogowany czy niezalogowany
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Toast.makeText(LoginActivity.this, "Jesteś zalogowany", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this, "Zaloguj się", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //obsługa navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent registration = new Intent(LoginActivity.this, RegisterActivity.class);
                switch (item.getItemId())
                {
                    case R.id.nav_login:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
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

        //logowanie
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = log_email.getText().toString();
                String password = log_password.getText().toString();
                if(required_fields_ok(email, password)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).
                            addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException e) {
                                    log_email.setError("Niepoprawny adres email!");
                                    log_email.requestFocus();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    log_password.setError("Niepoprawne hasło!");
                                    log_password.requestFocus();
                                } catch (FirebaseNetworkException e) {
                                    Toast.makeText(LoginActivity.this, "Błąd sieci! Sprawdź swoje połączenie", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "Niepowodzenie, spróbuj jeszcze raz", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                progressBar.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.VISIBLE);
                                already_logged = true;
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registration = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registration);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //czy puste pola przy logowaniu
    public boolean required_fields_ok (String email, String password)
    {
        int j = 0;
        if(email.isEmpty()) {
            log_email.setError("Wpisz email");
            log_email.requestFocus();
        }
        else j++;
        if(password.isEmpty()) {
            log_password.setError("Wpisz hasło");
            log_password.requestFocus();
        }
        else j++;
        if(j == 0) log_email.requestFocus();
        return j == 2;
    }


    //inicjalizacja zmiennych
    public void initialize (){
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        progressBar = findViewById(R.id.login_progress_bar);
        loading = findViewById(R.id.login_loading);
        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);
        log_btn = findViewById(R.id.log_btn);
        tv_register = findViewById(R.id.tv_register);

        View nav_header_title = navigationView.getHeaderView(0);
        TextView header_title = nav_header_title.findViewById(R.id.nav_header_title);
        header_title.setText(R.string.app_name);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_home).setVisible(false);
        nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        nav_Menu.findItem(R.id.nav_profile).setVisible(false);
        nav_Menu.findItem(R.id.nav_settings).setVisible(false);
    }
}
