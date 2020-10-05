from rest_framework import serializers
from .models import Profile

class ProfileSerializer(serializers.ModelSerializer):
	class Meta:
		model = Profile
		#fields = ('name','age','sex','movie1','movie2','movie3','elem1','elem2','elem3','place','country1','country2','country3',
		#'actor1','actor2','actor3','director1','director2','director3','is_oscar_winning','years1','years2','years3',
		#'food1','food2','food3','group')
		fields = '__all__'