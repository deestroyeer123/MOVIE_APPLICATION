package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;

import static com.example.movieapplication.RegisterActivity.users;

public class OptionActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    EditText op_name, op_surname, op_login, op_email;
    Button op_save_btn, op_edit_btn;
    ImageView img;
    Button op_photo_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.settings_title);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        op_name = findViewById(R.id.opt_name);
        op_surname = findViewById(R.id.opt_surname);
        op_login = findViewById(R.id.opt_login);
        op_email = findViewById(R.id.opt_email);
        op_save_btn = findViewById(R.id.op_save_btn);
        op_edit_btn = findViewById(R.id.op_edit_btn);

        View img_header = navigationView.getHeaderView(0);
        img = img_header.findViewById(R.id.header_img);

        op_photo_btn = findViewById(R.id.op_photo_btn);

        op_name.setEnabled(false);
        op_surname.setEnabled(false);
        op_login.setEnabled(false);
        op_email.setEnabled(false);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Menu nav_Menu = navigationView.getMenu();
        if(firebaseUser != null) {
            set_values();
            check_name_surname();
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_registration).setVisible(false);
        }
        else{
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
        }

        op_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 99);
            }
        });

        op_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                op_name.setEnabled(true);
                op_surname.setEnabled(true);
                op_login.setEnabled(true);
                op_email.setEnabled(true);
                /*
                LinearLayout.LayoutParams tableRowParams =
                        new TableLayout.LayoutParams(0,0);
                tableRowParams.setMargins(0,0,0,0);
                op_edit_btn.setLayoutParams(tableRowParams);
                op_edit_btn.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams tableRowParams2 =
                        new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(100,0,0,0);
                op_save_btn.setLayoutParams(tableRowParams2);
                op_save_btn.setVisibility(View.VISIBLE);

                 */
            }
        });

        op_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = op_name.getText().toString();
                String surname = op_surname.getText().toString();
                op_name.setText(name);
                op_surname.setText(surname);
                //update jakiś
                //write_val("name", name);
                //write_val("surname", surname);
                op_name.setEnabled(false);
                op_surname.setEnabled(false);
                op_login.setEnabled(false);
                op_email.setEnabled(false);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent login = new Intent(OptionActivity.this, LoginActivity.class);
                Intent registration = new Intent(OptionActivity.this, RegisterActivity.class);
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
                    case R.id.nav_login:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(login);
                        return true;
                    case R.id.nav_registration:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        startActivity(registration);
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
                op_email.setText(user.get_email());
                op_login.setText(user.get_login());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
        databaseReference.removeEventListener(postListener);

    }

    public void check_name_surname (){
        final String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = databaseReference.child(users).child(userID);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =  snapshot.getValue(User.class);
                assert user != null;
                //sprawdzenie czy wypelnione
                //if(!(user.get_name().equals(""))) op_name.setText(user.get_name());
                //if(!(user.get_surname().equals(""))) op_surname.setText(user.get_surname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(postListener);
        databaseReference.removeEventListener(postListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            //int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;

            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bmp != null;
            img.setImageBitmap(null);
            img.setBackground(null);
            img.setImageBitmap(bmp);

        }


    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void write_val(String field, String val) {
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference.child(users).child(userID).child(field).setValue(val);
    }
}
