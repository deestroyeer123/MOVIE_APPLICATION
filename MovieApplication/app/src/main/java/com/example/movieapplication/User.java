package com.example.movieapplication;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String login;
    public String email;
    public String name;
    public String surname;

    public User() {

    }

    public User(String login, String email, String name, String surname) {
        this.login = login;
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public String get_login() {
        return login;
    }

    public String get_email() {
        return email;
    }

    public String get_name() {
        return name;
    }

    public String get_surname() {
        return surname;
    }


}
