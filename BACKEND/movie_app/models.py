from django.db import models
import uuid

""" class Movies(models.Field):
    movie1 = models.CharField(max_length=100)
    movie2 = models.CharField(max_length=100)
    movie3 = models.CharField(max_length=100)
    def __init__(self, m1, m2, m3):
        self.movie1 = m1
        self.movie2 = m2
        self.movie3 = m3
class Elements(models.Model):
    elem1 = models.CharField(max_length=100)
    elem2 = models.CharField(max_length=100)
    elem3 = models.CharField(max_length=100)
class Countries(models.Model):
    country1 = models.CharField(max_length=100)
    country2 = models.CharField(max_length=100)
    country3 = models.CharField(max_length=100)
class Actors(models.Model):
    actor1 = models.CharField(max_length=100)
    actor2 = models.CharField(max_length=100)
    actor3 = models.CharField(max_length=100)
class Directors(models.Model):
    director1 = models.CharField(max_length=100)
    director2 = models.CharField(max_length=100)
    director3 = models.CharField(max_length=100)
class Years(models.Model):
    years1 = models.CharField(max_length=100)
    years2 = models.CharField(max_length=100)
    years3 = models.CharField(max_length=100)
class Foods(models.Model):
    food1 = models.CharField(max_length=100)
    food2 = models.CharField(max_length=100)
    food3 = models.CharField(max_length=100) """

class ProfileDetails(models.Model):
    uid = models.CharField(max_length=600)

class Profile(models.Model):
    name = models.CharField(max_length=400)
    age = models.PositiveIntegerField()
    sex = models.CharField(max_length=100)
    movie1 = models.CharField(max_length=100)
    movie2 = models.CharField(max_length=100)
    movie3 = models.CharField(max_length=100)
    elem1 = models.CharField(max_length=100)
    elem2 = models.CharField(max_length=100)
    elem3 = models.CharField(max_length=100)
    place = models.CharField(max_length=100)
    country1 = models.CharField(max_length=100)
    country2 = models.CharField(max_length=100)
    country3 = models.CharField(max_length=100)
    actor1 = models.CharField(max_length=100)
    actor2 = models.CharField(max_length=100)
    actor3 = models.CharField(max_length=100)
    director1 = models.CharField(max_length=100)
    director2 = models.CharField(max_length=100)
    director3 = models.CharField(max_length=100)
    oscar = models.BooleanField()
    years1 = models.CharField(max_length=100)
    years2 = models.CharField(max_length=100)
    years3 = models.CharField(max_length=100)
    food1 = models.CharField(max_length=100)
    food2 = models.CharField(max_length=100)
    food3 = models.CharField(max_length=100)
    group = models.CharField(max_length=100)

    #movies = Movies(models.CharField, models.CharField, models.CharField)
    #elements = Elements(elem1, elem2, elem3)
    #countries = Countries(country1, country2, country3)
    #actors = Actors(actor1, actor2, actor3)
    #directors = Directors(director1, director2, director3)
    #years = Years(years1, years2, years3)
    #foods = Foods(food1, food2, food3)

    def __str__(self):
        return self.name