from django.contrib import admin
from .models import Profile, ProfileDetails, User, UserStorage, Groups
# Register your models here.
admin.site.register(Profile)
admin.site.register(ProfileDetails)
admin.site.register(User)
admin.site.register(UserStorage)
admin.site.register(Groups)