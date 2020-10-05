package com.example.movieapplication;

public class Profile {
    public String name;
    public int age;
    public String sex;
    Movies movies;
    Elements elements;
    public String place;
    Countries countries;
    Actors actors;
    Directors directors;
    public boolean is_oscar_winning;
    Years years;
    Foods foods;
    public String group;

    public Profile() {

    }

    public Profile(String name, int age, String sex, String movie1, String movie2, String movie3,
    String elem1, String elem2, String elem3, String place, String country1, String country2, String country3,
                   String actor1, String actor2, String actor3, String director1, String director2, String director3,
    boolean oscar, String years1, String years2, String years3, String food1, String food2, String food3, String group) {

        this.name = name;
        this.age = age;
        this.sex = sex;
        movies = new Movies(movie1, movie2, movie3);
        elements = new Elements(elem1, elem2, elem3);
        this.place = place;
        countries = new Countries(country1, country2, country3);
        actors = new Actors(actor1, actor2, actor3);
        directors = new Directors(director1, director2, director3);
        this.is_oscar_winning = oscar;
        years = new Years(years1, years2, years3);
        foods = new Foods(food1, food2, food3);
        this.group = group;
    }

}
