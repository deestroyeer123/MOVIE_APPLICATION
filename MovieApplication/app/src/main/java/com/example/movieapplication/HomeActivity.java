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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.movieapplication.RegisterActivity.users;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference databaseReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView home_header;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.home_title);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_registration).setVisible(false);

        home_header = findViewById(R.id.welcome);

        set_values();


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
                        startActivity(log_out);
                        return true;
                }

                return false;
            }
        });

        /*
        final String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = databaseReference.child(users).child(userID).child("login");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.getValue(String.class);
                home_header.append(" " + val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

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
        final String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = databaseReference.child(users).child(userID);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                View nav_header = navigationView.getHeaderView(0);
                TextView header = nav_header.findViewById(R.id.nav_header);
                assert user != null;
                header.setText(user.get_login());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
        databaseReference.removeEventListener(postListener);

    }


}
