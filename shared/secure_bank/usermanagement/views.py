from usermanagement.models import User
from usermanagement.serializers import UserSerializer
from rest_framework import viewsets
from rest_framework import permissions


USER_TYPES = ["tier2", "tier1", "external"]


class CanCreateOrEditUser(permissions.BasePermission):
	"""
	Custom permission that allow admin or only user to update their profile
	"""

	def has_object_permission(self, request, view, obj):
		if request.method in permissions.SAFE_METHODS:
			return True

		if request.method == 'PUT':
			return obj.username == request.user.username

		return False


# ViewSets define the view behavior.
class UserViewSet(viewsets.ModelViewSet):
	queryset = User.objects.all()
	serializer_class = UserSerializer
	permission_classes = (permissions.IsAuthenticated, CanCreateOrEditUser,)

	
	def pre_save(self, obj):
		# set the use type
		if self.request.user.is_superuser:
			if not hasattr(self.request.user, "uType") or self.request.user.uType not in USER_TYPES:
				obj.uType = 'tier2'
		elif hasattr(self.request.user, "uType") and self.request.user.uType == "tier2":
			if not hasattr(self.request.user, "uType") or self.request.user.uType not in USER_TYPES[1:]:
				obj.uType = 'tier1'
		else:
			obj.uType = 'external'

	
	def post_save(self, obj, created=False):
		if created:
			u = User.objects.get(username__exact=obj.username)
			u.set_password(obj.password)
			u.save()