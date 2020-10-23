import pyrebase
from .models import Profile
from .serializers import ProfileSerializer
import json

config = {
  "apiKey": "apiKey",
  "authDomain": "projectId.firebaseapp.com",
  "databaseURL": "https://movieapplication-248de.firebaseio.com",
  "storageBucket": "projectId.appspot.com",
  "serviceAccount": "movie_app/firebase_sdk.json"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

class DatabaseHelper():

    tabele_name = "Profiles"
    uID = ""

    def insert(values, userID):
        db.child("Profiles").child(userID).set(values)

    def update(values, userID):
        db.child("Profiles").child(userID).update(values)

    def getVal(userID):
        values = db.child("Profiles").get()
        if values.val() == None:
            return False
        else:
            for x in values.val():
                if x == userID:
                    return True
            return False

    def getProfile(userID):
        profile = db.child("Profiles").child(userID).get()
        return profile.val()

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

    def createUser(values, userID):
        db.child("Users").child(userID).set(values)

    def getUserDetails(userID):
        user = db.child("Users").child(userID).get()
        return user.val()

    def putImg(userID, val):
        #storage = firebase.storage()
        #storage.child("Users").child(userID).child("images").put(img)
        db.child("Images").child(userID).set(val)

    def initialize():
        files = ["actors", "countries", "directors", "elements", "foods", "groups", "movies", "places", "sexes", "years"]
        tables = ["Actors", "Countries", "Directors", "Elements", "Foods", "Groups", "Movies", "Places", "Sexes", "Year"]
        for (x, y) in zip(files, tables):
            file = open("movie_app/" + x + ".json", encoding='utf-8')
            data = json.load(file)
            db.child("BaseProfile").child(y).set(data)
            file.close() 

    def removeBase():
        path = db.child("BaseProfile").get()
        if path.val() == None:
            pass
        else:
           db.child("BaseProfile").remove() 

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

    def updateGrups(userID, values):
        db.child("Groups").child(userID).update(values)

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

    def groupsExist(userID):
        values = db.child("Groups").get()
        if values.val() == None:
            return False
        else:
            for x in values.val():
                if x == userID:
                    return True
            return False