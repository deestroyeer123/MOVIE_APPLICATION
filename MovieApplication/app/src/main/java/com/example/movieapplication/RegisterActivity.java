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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    EditText reg_login, reg_email, reg_password, reg_password2;
    Button reg_btn;

    FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    public static String users = "users";

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

        reg_login = findViewById(R.id.log_login);
        reg_password = findViewById(R.id.log_password);
        reg_password2 = findViewById(R.id.log_password_confirm);
        reg_email = findViewById(R.id.log_email);
        reg_btn = findViewById(R.id.log_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String login = reg_login.getText().toString();
                String password = reg_password.getText().toString();
                String password2 = reg_password2.getText().toString();
                final String email = reg_email.getText().toString();
                if(required_fields_ok(login, email, password, password2)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Niepowodzenie, spróbuj jeszcze raz", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                writeNewUser(userID, login, email);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(login)
                                        //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Zaktualizowano", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }


            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                Intent settings = new Intent(RegisterActivity.this, OptionActivity.class);
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
                    case R.id.nav_settings:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(settings);
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

    private void writeNewUser(String userID, String login, String email) {
        User user = new User(login, email, "", "");

        databaseReference.child(users).child(userID).setValue(user);
    }
}
