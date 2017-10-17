from django.db import models
from django.contrib.auth.models import AbstractUser
from djangotoolbox.fields import ListField

# Create your models here.
class User(AbstractUser):
	uType = models.CharField(max_length=10)
	accounts = ListField()


class Account(models.Model):
	# account# will be _id
	amount = models.FloatField()
	aType = models.CharField(max_length=50)