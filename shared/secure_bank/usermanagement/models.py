from django.db import models

# Create your models here.

from django.contrib.auth.models import AbstractUser


class SecureUser(AbstractUser):
    role = models.CharField(max_length=250)  # "tier1","tier2","administrator","merchant","individual"
