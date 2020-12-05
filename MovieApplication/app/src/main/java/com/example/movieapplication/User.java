package com.example.movieapplication;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String login;
    public String email;

    public User() {

    }

    public User(String login, String email) {
        this.login = login;
        this.email = email;
    }

    public String get_login() {
        return login;
    }
    public String get_email() {
        return email;
    }
}
