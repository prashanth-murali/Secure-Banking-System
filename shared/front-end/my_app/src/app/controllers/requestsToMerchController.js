module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', '$state', function ($scope, $http, authService, $mdToast, $stateParams, $sessionStorage,$state) {
    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    /**function randomString() {
        return Math.floor(Math.random()*90000) + 10000 + "";
    }**/


    function getAccountsForUser() {
        return $http.get(BACKEND_URL + '/api/accounts/user/'+ authService.getUserId(), {
            headers: {
                "authorization": authService.getAuth()
            }

        });
    }

    function getAllMerchants(){
        return $http.get(BACKEND_URL + '/api/transactions/payments', {
            headers: {
                "authorization": authService.getAuth()
            }

        });
    }

    $scope.approvePayment=function(TransactionId){
        return $http.put(BACKEND_URL + '/api/transactions/payments/requests/'+TransactionId, {
            "status":"approved"
        },{
            headers: {
                "authorization": authService.getAuth()
            }

        }).then(function successCallback(){
            alert('Transaction Approved');
        }, function errorCallback(response){
            if(response.status!=200){
                alert('Error!');
            }
        });
    }

    $scope.filterMerchant=function(transaction)
    {
        if(transaction.status=='pending')
        {
            return true;
        }
        return false;
    };

    $scope.declinePayment=function(TransactionId){
        return $http.put(BACKEND_URL + '/api/transactions/payments/requests/'+TransactionId, {
            "status":"denied"
        },{
            headers: {
                "authorization": authService.getAuth()
            }

        }).then(function successCallback(){
            alert('Transaction Declined');
        }, function errorCallback(response){
            if(response.status!=200){
                alert('Error!');
            }
        });
    }


    function fetchAllmerchants(){
        getAllMerchants().then(function successCallback(response){
                $scope.merchants = response.data;
            },
            function errorCallback(response){
                if(response.status!=200)
                {
                    toast('Error Fetching Merchants!');
                }
            });
    }

    $scope.payMerchant=function(cardId,cvv,amount,merchant){
        return $http.post(BACKEND_URL + '/api/transactions/payments',{
            "creditCard": cardId,
            "amount": amount,
            "toAccountId": merchant,
            "cvv":cvv

        }, {
            headers: {
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
        if(fromId!=toId && $scope.Amount>0)
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



        else {alert('Sender and Receiver Account Id cannot be the same/ Amount cannot be negative.');}
    }

    function getAllTransactionsByAccount(AccountId){
        return $http.get(BACKEND_URL + '/api/transactions/account/'+ AccountId, {
            headers: {
                "authorization": authService.getAuth()
            }

        });
    }

    $scope.fetchAllTransactionsByAccount=function(AccountId){
        getAllTransactionsByAccount(AccountId).then(function successCallback(response) {
            $scope.statements = response.data;
        }, function errorCallback(response) {
            toast('Error loading statement');
        });
    }

    $scope.addMoney=function(AccountId)
    {
        var data=prompt("Enter_Amount","1000");
        if(data<1){
            alert('Please Enter a Valid Amount!');
        }
        if(data>=1){
            $scope.sendAddMoneyReq(AccountId,data).then(function success(){
                alert('Withdrawal Successful.');
            },function errorCallback(response){
                if(response.status!=200)
                {
                    alert('Please Enter a Valid Amount.');
                }
            });
        }

    }

    $scope.sendAddMoneyReq = function(AccountId,Amount){
        return $http.post(BACKEND_URL+'/api/transactions/',{
            "fromAccountId": AccountId,
            "toAccountId": AccountId,
            "type": "debit",
            "amount": Amount

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        }).then(function success(){
            toast("created transaction!");
        },function onError(){
            toast("error creating transaction");
        });
    };

    $scope.withdrawMoney=function(AccountId){
        var data=prompt("Enter_Amount","1000");
        $scope.sendWithdrawMoneyReq(AccountId,data).then(function success(){
            alert('Withdrawal Successful.');
        },function errorCallback(response){
            if(response.status!=200)
            {
                alert('Please Enter a Valid Amount.');
            }
        });
    };

    $scope.sendWithdrawMoneyReq = function(AccountId,Amount){
        return $http.post(BACKEND_URL+'/api/transactions/',{
            "fromAccountId": AccountId,
            "toAccountId": AccountId,
            "type": "debit",
            "amount": -Amount

        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.exportAction = function(accountId){
        $http({
            method: 'GET',
            url: BACKEND_URL+'/api/transactions/statement/'+accountId,
            headers:{
                "authorization": authService.getAuth()
            }
        }).then(function(response) {
            console.log(response);
            var anchor = angular.element('<a/>');
            anchor.attr({
                href: 'data:attachment/csv;charset=utf-8,' + encodeURI(response.data),
                target: '_blank',
                download: 'filename.csv'
            })[0].click();

        },function(data, status, headers, config) {
            toast("Error fetching statement")
        });
    };

    $scope.filterCredit=function(account)
    {
        if(account.accountType=='credit')
        {
            return true;
        }
        return false;
    };

    $scope.getRequests=function(){
        return $http.get(BACKEND_URL + '/api/transactions/payments/requests', {
            headers: {
                "authorization": authService.getAuth()
            }

        });
    }

    function fetchAllRequests(){
        $scope.getRequests().then(function successCallback(response){
                $scope.merchReqs = response.data;
            },
            function errorCallback(response){
                if(response.status!=200)
                {
                    toast('Error Fetching Merchants!');
                }
            });
    }


    function fetchAccounts() {
        getAccountsForUser().then(function successCallback(response) {
            $scope.accounts = response.data;
        }, function errorCallback(response) {
            toast('Error loading accounts');
        });
    }

    fetchAllRequests();

}];

