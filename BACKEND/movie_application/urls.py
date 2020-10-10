from django.contrib import admin
from django.urls import path
from movie_app import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path('profile/', views.profileView.as_view()),
    path('profile/details/', views.profileDetails.as_view()),
    #path('profile/<uid>/edit/', views.profileUpdate.as_view()),
    #path('profile/create/', views.profileCreate.as_view()),
    path('profile/load/', views.loadProfile.as_view()),
    path('profile/cos/', views.cos),
]
