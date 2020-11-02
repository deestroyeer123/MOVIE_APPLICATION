from django.db import models

class Groups(models.Model):
    knn = models.IntegerField()
    kmeans = models.IntegerField()
class UserStorage(models.Model):
    img = models.ImageField(upload_to="media/images/")

class User(models.Model):
    login = models.CharField(max_length=100)
    email = models.CharField(max_length=100)

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

    def __str__(self):
        return self.name
