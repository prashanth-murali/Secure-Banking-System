from django.contrib.auth.models import User
from rest_framework import serializers

# Serializers define the API representation.
from usermanagement.models import SecureUser


class UserSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = SecureUser
		fields = ('url', 'username', 'email', 'is_staff', 'role')