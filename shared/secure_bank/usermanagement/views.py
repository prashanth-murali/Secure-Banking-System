from usermanagement.models import SecureUser
from usermanagement.serializers import UserSerializer
from rest_framework import viewsets

# ViewSets define the view behavior.
class UserViewSet(viewsets.ModelViewSet):
	queryset = SecureUser.objects.all()
	serializer_class = UserSerializer