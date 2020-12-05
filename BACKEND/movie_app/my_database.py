import pyrebase
from .models import Profile
from .serializers import ProfileSerializer
import json

config = {
  "apiKey": "AIzaSyC0kIXsozFH-rYjO2uFNG_KEWsgYBufsWI",
  "authDomain": "movieapplication-248de.firebaseapp.com",
  "databaseURL": "https://movieapplication-248de.firebaseio.com",
  "storageBucket": "movieapplication-248de.appspot.com",
  "serviceAccount": "movie_app/firebase_sdk.json"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()
storage = firebase.storage()

class DatabaseHelper():

    tabele_name = "Profiles"
    uID = ""

    #dodanie nowego profilu użytkownika
    def insert(values, userID):
        db.child("Profiles").child(userID).set(values)

    #zaktualizowanie profilu użytkownika
    def update(values, userID):
        db.child("Profiles").child(userID).update(values)

    #sprawdzenie czy profil uzytkownika o okreslonym uid istnieje
    def getVal(userID):
        values = db.child("Profiles").get()
        if values.val() == None:
            return False
        else:
            for x in values.val():
                if x == userID:
                    return True
            return False

    #pobranie profilu uzytkownika o okreslonym uid
    def getProfile(userID):
        profile = db.child("Profiles").child(userID).get()
        return profile.val()

    #podranie profili wszystkich uzytkowników
    def getProfiles():
        listProfiles = []
        listValues = []
        profiles = db.child("Profiles").get()
        for x in profiles.val():
            values = db.child("Profiles").child(x).get()
            for y in values.val():
                listValues.append(values.val()[y])
            del listValues[17]
            del listValues[20]
            listProfiles.append(listValues)
            listProfiles.append(x)
            listValues = []
        return listProfiles

    #dodanie nowego użytkownika
    def createUser(values, userID):
        db.child("Users").child(userID).set(values)

    #pobranie emaila i loginu użytkownika
    def getUserDetails(userID):
        user = db.child("Users").child(userID).get()
        return user.val()

    #zapisanie nowego zdjęcia w formie base64
    def putImg(userID, img):
        db.child("Images").child(userID).set(img)

    #zaktualizowanie zdjecie w formie base64
    def updateImg(userID, img):
        db.child("Images").child(userID).remove()
        db.child("Images").child(userID).set(img)

    #zainicjalizowanie szablonu z baza wyborów
    def initialize():
        files = ["actors", "countries", "directors", "elements", "foods", "groups", "movies", "places", "sexes", "years"]
        tables = ["Actors", "Countries", "Directors", "Elements", "Foods", "Groups", "Movies", "Places", "Sexes", "Year"]
        for (x, y) in zip(files, tables):
            file = open("movie_app/" + x + ".json", encoding='utf-8')
            data = json.load(file)
            db.child("BaseProfile").child(y).set(data)
            file.close() 

    #usunięcie szablonu z baza wyborów
    def removeBase():
        path = db.child("BaseProfile").get()
        if path.val() == None:
            pass
        else:
           db.child("BaseProfile").remove() 

    #pobranie szablonu z baza wyborów
    def getBase():
        baseList = []
        baseValues = []
        base = db.child("BaseProfile").get()
        for x in base.val():
            values = db.child("BaseProfile").child(x).get()
            for y in values.val():
                baseValues.append(values.val()[y])
            baseList.append(baseValues)
            baseValues = []
        return baseList

    #zaktualizowanie/stworzenie grupy dla użytkownika o okreslonym uid
    def updateGrups(userID, values):
        db.child("Groups").child(userID).update(values)

    #pobranie profilu użytkownika o określonym uid przygotowanego do klasyfikacji
    def getProfileToClassify(userID):
        listProfiles = []
        listValues = []
        profile = db.child("Profiles").child(userID).get()
        for y in profile.val():
            listValues.append(profile.val()[y])
        del listValues[17]
        del listValues[20]
        listProfiles.append(listValues)
        listValues = []
        return listProfiles

    #pobranie grup wszystkich użytkowników
    def getUsersGroups():
        listGroups = []
        listValues = []
        groupsUsers = db.child("Groups").get()
        for x in groupsUsers.val():
            values = db.child("Groups").child(x).get()
            for y in values.val():
                listValues.append(values.val()[y])
            listGroups.append(listValues)
            listGroups.append(x)
            listValues = []
        return listGroups

    #sprawdzenie czy użytkownik ma przydzielona grupe
    def groupsExist(userID):
        values = db.child("Groups").get()
        if values.val() == None:
            return False
        else:
            for x in values.val():
                if x == userID:
                    return True
            return False

    #pobranie zdjecia
    def getImg(userID):
        img = db.child("Images").child(userID).get()
        return img.val()

    #sprawdzenie czy uzytkownik ma zdjecie
    def imageExist(userID):
        img = db.child("Images").child(userID).get()
        if img.val() == None:
            return False
        return True