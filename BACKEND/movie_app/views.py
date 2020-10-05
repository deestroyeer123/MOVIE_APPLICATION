from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.generics import UpdateAPIView 
from rest_framework.generics import CreateAPIView
from rest_framework.response import Response
from .models import Profile
from .serializers import ProfileSerializer
#from .firebasDB import FirebaseDB()


class profileView(APIView):

    def get(self, request):
        profile = Profile.objects.all()
        serializer = ProfileSerializer(profile, many=True)
        return Response(serializer.data)

    def post(self):
        pass

class profileUpdate(UpdateAPIView):

    queryset = Profile.objects.all()
    serializer_class = ProfileSerializer
    lookup_field = 'id'

class profileCreate(CreateAPIView):
    queryset = Profile.objects.all()
    serializer_class = ProfileSerializer


def cos(request):
    #insert()
    return render(request, 'movie_app/cos.html')