from usermanagement.models import User
from rest_framework import serializers


class UserSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = User
		fields = ('url', 'username', 'email', 'password', 'first_name', 'last_name', 'is_staff', 'uType')
		write_only_fields = ('password',)

	# def restore_object(self, attrs, instance=None):
	# 	"""
	# 	Instantiate a new User instance.
	# 	"""
	# 	if instance is not None:
	# 		instance.username = attrs.get('username', instance.username)
	# 		instance.email = attrs.get('email', instance.email)
	# 		instance.first_name = attrs.get('first_name', instance.first_name)
	# 		instance.last_name = attrs.get('last_name', instance.last_name)
	# 		instance.is_staff = attrs.get('is_staff', instance.is_staff)
	# 		instance.type = attrs.get('type', instance.type)
	# 		instance.set_password(attrs.get('password', instance.password))
	# 		return instance


	# 	user = User(**attrs)
	# 	user.set_password(attrs['password'])
		
	# 	return user
