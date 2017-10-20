from bson import ObjectId
from django.conf import settings

from usermanagement.models import User, Account, Transaction, Config
from usermanagement.serializers import UserSerializer, AccountSerializer, TransactionSerializer
from django.db.models import Q
from rest_framework import viewsets
from rest_framework import permissions
from rest_framework.views import APIView
from rest_framework.decorators import detail_route
from rest_framework.response import Response
import json
import logging


USER_TYPES = ["tier2", "tier1", "administrator", "external"]
USER_RANK = {"tier2": 2, "tier1": 1, "administrator": 3, "external": 0}
ACC_TYPES = ["current", "savings", "credit"]



def getCriticalLimit():
	conf = Config.objects.get(key__exact="critical_transaction_limit")
	if not conf:
		conf = Config(
			key="critical_transaction_limit",
			val=settings.CRITICAL_TRANSACTION_LIMIT,
			valType="float"
		)

		conf.save()

	return conf.val



class CanCreateEditDeleteUser(permissions.BasePermission):
	"""
	Custom permission that allow admin or only user to update their profile
	"""

	def has_object_permission(self, request, view, obj):
		if request.method in permissions.SAFE_METHODS:
			return True

		if request.method == 'PUT':
			return obj.username == request.user.username

		if request.method == 'DELETE':
			cu = User.objects.get(id__exact=request.user.id)
			if cu.uType == 'external' or cu.uType == '':
				return False
			elif obj.uType == '':
				return True
			else:
				return USER_RANK[cu.uType] > USER_RANK[obj.uType]

		return False


class CanCreateAccount(permissions.BasePermission):
	def has_permission(self, request, view):
		u = User.objects.get(id__exact=request.user.id)

		if u.uType == "":
			return False

		if u.uType == "external":
			return view.kwargs.get('pk') == request.user.id

		return True


class CanEditOrDeleteAccount(permissions.BasePermission):
	def has_object_permission(self, request, view, obj):
		if request.method in permissions.SAFE_METHODS:
			return True

		u = User.objects.get(id__exact=request.user.id)

		if u.uType == "" or u.uType == "external":
			return False

		return True


class CanEditTransaction(permissions.BasePermission):
	def has_object_permission(self, request, view, obj):
		if request.method in permissions.SAFE_METHODS:
			return True

		u = User.objects.get(id__exact=request.user.id)

		if u.uType == "" or u.uType == "external":
			return False

		return True

	def has_permission(self, request, view):
		return request.method != 'POST'


class CanCreditOrDebitAccount(permissions.BasePermission):
	def has_permission(self, request, view):
		u = User.objects.get(id__exact=request.user.id)

		if u.uType == "" or u.uType == "external":
			return (view.kwargs.get('pk') in u.accounts)

		return True


# ViewSets define the view behavior.
class UserViewSet(viewsets.ModelViewSet):
	queryset = User.objects.all()
	serializer_class = UserSerializer
	permission_classes = (permissions.IsAuthenticated, CanCreateEditDeleteUser,)


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
		u = User.objects.get(username__exact=obj.username)
		u.set_password(obj.password)
		u.save()


	@detail_route(methods=['post'], permission_classes=[permissions.IsAuthenticated, CanCreateAccount])
	def open_account(self, request, pk=None):
		aType = request.POST['atype']

		if not aType in ACC_TYPES:
			aType = ACC_TYPES[0]

		acc = Account.objects.create(amount=0.0, aType=aType)
		acc.save()

		u = User.objects.get(id__exact=pk)
		u.accounts.extend([acc.id])
		u.save()

		return Response({'status': 'ok'})


class AccountViewSet(viewsets.ModelViewSet):

	model = Account
	serializer_class = AccountSerializer
	permission_classes = (permissions.IsAuthenticated, CanEditOrDeleteAccount)

	def get_queryset(self):
		u = User.objects.get(id__exact=self.request.user.id)

		if u.uType == "external":
			return Account.objects.filter(id__in=u.accounts)

		return Account.objects.all()

	def pre_save(self, obj):
		#validate that the object user_id is a valid user
		u = User.objects.get(id__exact=ObjectId(obj.user_id))
		if u is None:
			raise Exception('user with id :'+obj.user_id+'does not exist')


	def creditAccount(accID, amount):
		amount = max(amount, 0.0)
		acc = Account.objects.get(id__exact=accID)
		acc.amount = acc.amount + amount
		acc.save()

	def debitAccount(accID, amount):
		amount = max(amount, 0.0)
		acc = Account.objects.get(id__exact=accID)

		if acc.amount > amount:
			acc.amount = acc.amount - amount
			acc.save()
			return True

		return False


	@detail_route(methods=['post'], permission_classes=[permissions.IsAuthenticated, CanCreditOrDebitAccount])
	def credit(self, request, pk=None):
		if 'amount' in request.POST:
			amount = float(request.POST['amount'])
			self.creditAccount(pk, amount)

			# save transaction
			t = Transaction.objects.create(amount=amount, destination=pk, status='approved', ttype='credit')
			t.save()
			
			return Response({'status': 'ok'})

		return Response({'status': 'need amount'})


	@detail_route(methods=['post'], permission_classes=[permissions.IsAuthenticated, CanCreditOrDebitAccount])
	def debit(self, request, pk=None):
		if 'amount' in request.POST:
			amount = float(request.POST['amount'])
			if self.debitAccount(pk, amount):
				
				# save transaction
				t = Transaction.objects.create(amount=amount, source=pk, status='approved', ttype='debit')
				t.save()

				return Response({'status': 'ok'})
			else:
				return Response({'status': 'not enough money'})

		return Response({'status': 'need amount'})


	@detail_route(methods=['post'], permission_classes=[permissions.IsAuthenticated, CanCreditOrDebitAccount])
	def transfer(self, request, pk=None):
		if 'amount' in request.POST and 'to' in request.POST:
			if pk != request.POST['to']:
				amount = float(request.POST['amount'])
				if self.debitAccount(pk, amount):
					self.creditAccount(request.POST['to'], amount)

					# save transaction
					t = Transaction.objects.create(amount=amount, source=pk, destination=request.POST['to'], status='approved', ttype='transfer')
					t.save()

					return Response({'status': 'ok'})
				else:
					return Response({'status': 'not enough money'})

		return Response({'status': 'not enough info'})


class TransactionViewSet(viewsets.ModelViewSet):

	model = Transaction
	serializer_class = TransactionSerializer
	permission_classes = (permissions.IsAuthenticated, CanEditTransaction)

	def get_queryset(self):
		u = User.objects.get(id__exact=self.request.user.id)

		if u.uType == "external":
			return Transaction.objects.filter(Q(source=u.id) | Q(destination=u.id))

		return Transaction.objects.all()