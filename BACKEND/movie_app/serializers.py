from rest_framework import serializers
from .models import Profile, ProfileDetails

class ProfileSerializer(serializers.ModelSerializer):
	class Meta:
		model = Profile
		fields = '__all__'

class ProfileDetailsSerializer(serializers.ModelSerializer):
	class Meta:
		model = ProfileDetails
		fields = '__all__'