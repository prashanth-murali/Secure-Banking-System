import json

from rest_framework.response import Response
from rest_framework.views import APIView


class Login(APIView):
    def get(self, request):
        return Response({'userId': request.user.id})
