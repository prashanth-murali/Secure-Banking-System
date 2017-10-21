from usermanagement.models import User, Account, Transaction
from rest_framework import serializers


class UserSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = User
		fields = ('url', 'username', 'email', 'password', 'first_name', 'last_name', 'is_staff', 'uType', 'accounts', 'isMerchant')
		write_only_fields = ('password',)

	def __init__(self, *args, **kwargs):
		# initialize fields
		super(UserSerializer, self).__init__(*args, **kwargs)
		
		# password is not required during update
		request = self.context.get("request")
		self.fields['password'].allow_none = True
		self.fields['password'].required = (request.method != 'PUT')


class AccountSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = Account
		fields = ('url', 'id','user_id' ,'amount', 'aType')
		read_only_fields = ('id',)


class TransactionSerializer(serializers.HyperlinkedModelSerializer):
	class Meta:
		model = Transaction
		fields = ('url', 'amount', 'source', 'destination', 'ttype', 'status')
		read_only_fields = ('amount', 'source', 'ttype', 'destination')