module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', '$state', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike, $state) {
    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }


    $scope.filterInternal=function(transaction)
    {
        if(transaction.status=='pending')
        {
            if(transaction.critical==false)
            {
                return true;
            }
        }
        return false;
    };

    $scope.filterUser=function(user)
    {
        if(user.type=='consumer' || user.type=='merchant')
        {
            return true;

        }
        return false;
    };

    function getUserById(UserId)
    {
        return $http.get(BACKEND_URL + '/api/users/'+UserId, {
            headers: {
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.fetchUserById=function(UserId){
        getUserById(UserId).then(function successCallback(response) {
            $scope.individual = response.data;
        }, function errorCallback(response) {
            toast('Error loading user details');
        });
    };

    $scope.filterManager=function(transaction)
    {
        if(transaction.status=='pending' && transaction.type!='credit')
        {
            return true;
        }
        return false;
    };

    $scope.approveTransaction = function(TransactionId)
    {

        return $http.put(BACKEND_URL + '/api/transactions/'+TransactionId,{
            "status":"approved"

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.declineTransaction = function(TransactionId)
    {

        return $http.put(BACKEND_URL + '/api/transactions/'+TransactionId,{
            "status":"denied"

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }


    $scope.postTransactionViaId = function(fromId,toId,transferType){
        return $http.post(BACKEND_URL+'/api/transactions/',{
            "fromAccountId": fromId,
            "toAccountId": toId,
            "type": transferType,
            "amount": $scope.Amount

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.postTransactionViaEmail = function(fromId,toId,transferType){
        return $http.post(BACKEND_URL+'/api/transactions/',{
            "fromAccountId": fromId,
            "email": toId,
            "type": transferType,
            "amount": $scope.Amount

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.createTransaction=function(fromId,toId,transferType){
        if(fromId!=toId)
        {

            if(transferType=="via_email")
            {
                $scope.postTransactionViaEmail(fromId,toId,transferType);
                alert('Request sent via email. Please Check Statements page for updates on the status of your transfer.');

            }
            else
            {
                $scope.postTransactionViaId(fromId, toId, transferType);
                alert('Request sent. Please Check Statements page for updates on the status of your transfer.');
            }
        }



        else {alert('Sender and Receiver Account Id cannot be the same');}
    }




    function getAllTransactions(){
        return $http.get(BACKEND_URL + '/api/transactions/', {
            headers: {
                "authorization": authService.getAuth()
            }

        });
    }

    function fetchTransactions(){
        getAllTransactions().then(function successCallback(response) {
            $scope.transactions = response.data;
        }, function errorCallback(response) {
            toast('Error loading transactions');
        });
    }

    fetchTransactions();

}];


