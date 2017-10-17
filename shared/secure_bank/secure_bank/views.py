import json

from rest_framework.response import Response
from rest_framework.views import APIView


class MyOwnView(APIView):
    def get(self, request):
        return Response({'some': request.user.id})