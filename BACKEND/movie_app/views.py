from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework import status
from rest_framework.generics import UpdateAPIView 
from rest_framework.generics import CreateAPIView
from rest_framework.response import Response
from django.http import Http404
from .models import Profile, ProfileDetails, User, Groups
from .serializers import ProfileSerializer, ProfileDetailsSerializer, UserSerializer, GroupsSerializer
from .my_database import DatabaseHelper
from .knn import Knn

class userStorage(APIView):
    def get(self):
        pass
    
    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        if serializer.is_valid():
            userID = DatabaseHelper.uID
            serializer.save()
            DatabaseHelper.putImg(userID, serializer.data)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class userDetails(APIView):
    def get(self, request):
        userID = DatabaseHelper.uID
        user = DatabaseHelper.getUserDetails(userID)
        return Response(user)
        
    def post(self):
        pass

class userView(APIView):

    def get(self, request):
        users = User.objects.all()
        serializer = UserSerializer(users, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            userID = DatabaseHelper.uID
            serializer.save()
            DatabaseHelper.createUser(serializer.data, userID)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class loadProfile(APIView):
    def get(self, request):
        userID = DatabaseHelper.uID
        profile = None
        if DatabaseHelper.getVal(userID):
            profile = DatabaseHelper.getProfile(userID)
            return Response(profile)
        else:
            print(profile)
            return Response(profile)
    def post(self):
        pass
class profileDetails(APIView):
    def get(self, request):
        profile = ProfileDetails.objects.all()
        serializer = ProfileDetailsSerializer(profile, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = ProfileDetailsSerializer(data=request.data)
        if serializer.is_valid():
            DatabaseHelper.uID = next(iter(serializer.data.values()))
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
class profileView(APIView):

    def get(self, request):
        profile = Profile.objects.all()
        serializer = ProfileSerializer(profile, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        if serializer.is_valid():
            userID = DatabaseHelper.uID
            if DatabaseHelper.getVal(userID):
                serializer.save()
                DatabaseHelper.update(serializer.data, userID)
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            serializer.save()
            DatabaseHelper.insert(serializer.data, userID)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class getMatchedUsers(APIView):
    def get(self, request):
        listProfiles = []
        users = Knn.setGroup()
        for x in users:
            profile = DatabaseHelper.getProfile(x)
            listProfiles.append(profile)
        return Response(listProfiles)
    def post(self):
        pass

def initializeBase(request):
    DatabaseHelper.removeBase()
    DatabaseHelper.initialize()
    return render(request, 'movie_app/cos.html')
    

def learnModel(request):
    #Knn.learn()
    return render(request, 'movie_app/cos.html')

    
