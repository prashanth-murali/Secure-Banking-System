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
	# account# will be id
	user_id = models.CharField(max_length=100)
	amount = models.FloatField()
	aType = models.CharField(
		max_length=20,
		choices=(
			("checking", "checking"),
			("savings", "savings"),
			("credit", "credit"),
		)
	)


class Transaction(models.Model):
	amount = models.FloatField()
	source = models.CharField(max_length=24, blank=True)
	destination = models.CharField(max_length=24, blank=True)
	ttype = models.CharField(
		max_length=10,
		choices=(
			('credit', 'credit'),
			('debit', 'debit'),
			('transfer', 'transfer')
		)
	)
	status = models.CharField(
		max_length=10,
		choices=(
			('approved', 'approved'),
			('pending', 'pending'),
			('rejected', 'rejected')
		)
	)