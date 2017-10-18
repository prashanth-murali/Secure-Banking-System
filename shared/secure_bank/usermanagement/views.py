from usermanagement.models import User, Account
from usermanagement.serializers import UserSerializer
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.decorators import detail_route
from rest_framework.response import Response
import json


USER_TYPES = ["tier2", "tier1", "administrator", "external"]
ACC_TYPES = ["current", "savings", "credit"]


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


	@detail_route(methods=['post'], permission_classes=[permissions.IsAuthenticated])
	def open_account(self, request, pk=None, aType=ACC_TYPES[0]):
		if not aType in ACC_TYPES:
			aType = ACC_TYPES[0]

		acc = Account.objects.create(amount=0.0, aType=aType)
		acc.save()

		u = User.objects.get(id__exact=pk)
		u.accounts.extend([acc.id])
		u.save()

		return Response({'status': 'ok'})
