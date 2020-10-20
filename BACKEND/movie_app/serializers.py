from rest_framework import serializers
from .models import Profile, ProfileDetails, User, UserStorage

class ProfileSerializer(serializers.ModelSerializer):
	class Meta:
		model = Profile
		fields = '__all__'

class ProfileDetailsSerializer(serializers.ModelSerializer):
	class Meta:
		model = ProfileDetails
		fields = '__all__'

class UserSerializer(serializers.ModelSerializer):
	class Meta:
		model = User
		fields = '__all__'

class UserStorageSerializer(serializers.ModelSerializer):
	class Meta:
		model = UserStorage
		fields = '__all__'