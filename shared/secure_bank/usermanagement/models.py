from django.db import models
from django.contrib.auth.models import AbstractUser
from djangotoolbox.fields import ListField

# Create your models here.
class User(AbstractUser):
	uType = models.CharField(
		max_length=20,
		choices=(
			('administrator', 'administrator'),
			("tier2", "tier2"),
			("tier1", "tier1"),
			("external", "external")
		)
	)
	accounts = ListField()
	isMerchant = models.BooleanField()


class Account(models.Model):
	# account# will be _id
	amount = models.FloatField()
	aType = models.CharField(max_length=50)