package com.example.movieapplication;

public class Profile {
    public String name;
    public int age;
    public String sex;
    public String movie1;
    public String movie2;
    public String movie3;
    public String elem1;
    public String elem2;
    public String elem3;
    public String place;
    public String country1;
    public String country2;
    public String country3;
    public String actor1;
    public String actor2;
    public String actor3;
    public String director1;
    public String director2;
    public String director3;
    public boolean oscar;
    public String years1;
    public String years2;
    public String years3;
    public String food1;
    public String food2;
    public String food3;
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
        this.movie1 = movie1;
        this.movie2 = movie2;
        this.movie3 = movie3;
        this.elem1 = elem1;
        this.elem2 = elem2;
        this.elem3 = elem3;
        this.place = place;
        this.country1 = country1;
        this.country2 = country2;
        this.country3 = country3;
        this.actor1 = actor1;
        this.actor2 = actor2;
        this.actor3 = actor3;
        this.director1 = director1;
        this.director2 = director2;
        this.director3 = director3;
        this.oscar = oscar;
        this.years1 = years1;
        this.years2 = years2;
        this.years3 = years3;
        this.food1 = food1;
        this.food2 = food2;
        this.food3 = food3;
        this.group = group;
    }

    public String get_name() { return name; }
    public int get_age() { return age; }
    public String get_sex() { return sex; }
    public String get_movie1() { return movie1; }
    public String get_movie2() { return movie2; }
    public String get_movie3() { return movie3; }
    public String get_elem1() { return elem1; }
    public String get_elem2() { return elem2; }
    public String get_elem3() { return elem3; }
    public String get_place() { return place; }
    public String get_country1() { return country1; }
    public String get_country2() { return country2; }
    public String get_country3() { return country3; }
    public String get_actor1() { return actor1; }
    public String get_actor2() { return actor2; }
    public String get_actor3() { return actor3; }
    public String get_director1() { return director1; }
    public String get_director2() { return director2; }
    public String get_director3() { return director3; }
    public boolean get_oscar() { return oscar; }
    public String get_years1() { return years1; }
    public String get_years2() { return years2; }
    public String get_years3() { return years3; }
    public String get_food1() { return food1; }
    public String get_food2() { return food2; }
    public String get_food3() { return food3; }
    public String get_group() { return group; }

}
