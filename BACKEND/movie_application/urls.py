from django.contrib import admin
from django.urls import path
from movie_app import views
from django.conf.urls.static import static
from django.conf import settings

urlpatterns = [
    path('admin/', admin.site.urls),
    path('profile/', views.profileView.as_view()),
    path('profile/details/', views.profileDetails.as_view()),
    #path('profile/<uid>/edit/', views.profileUpdate.as_view()),
    #path('profile/create/', views.profileCreate.as_view()),
    path('profile/load/', views.loadProfile.as_view()),
    path('user/', views.userView.as_view()),
    path('user/details/', views.userDetails.as_view()),
    path('profile/base/', views.initializeBase),
    path('user/storage/', views.userStorage.as_view()),
    path('profile/cos/', views.cos),
]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
