package com.example.movieapplication;

import android.graphics.Bitmap;

public class ListModel {

    private String name = "";
    private String age = "";
    private Bitmap img;

    public void set_name(String name) {
        this.name = name;
    }

    public void set_age(String age) {
        this.age = age;
    }

    public void set_img(Bitmap img) { this.img = img; }

    public String get_name() {
        return this.name;
    }

    public String get_age() { return this.age; }

    public Bitmap get_img() {
        return this.img;
    }

}
