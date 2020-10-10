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
        #return profile

        