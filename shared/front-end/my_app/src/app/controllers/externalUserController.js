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

    $scope.postTransaction = function(fromId,toId,transferType){
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

    $scope.createTransaction=function(fromId,toId,transferType){
        if(fromId!=toId)
        {
            $scope.postTransaction(fromId,toId,transferType);
            alert('Request sent. Please Check Statements page for updates on the status of your transfer.');
        }

        else {alert('Sender and Receiver Account Id cannot be the same');}
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
        $scope.sendAddMoneyReq(AccountId,data);

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
        });
    }

    $scope.withdrawMoney=function(AccountId){
        var data=prompt("Enter_Amount","1000");
        $scope.sendWithdrawMoneyReq(AccountId,data);
    }

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


    /**function setRandomCreateData() {
        $scope.create = {
            "username": randomString(),
            "email": randomString()+"@abc.com",
            "password": "password",
            "firstName": randomString(),
            "lastName": randomString(),
            "is_staff": false,
            "uType": "tier1",
            "isMerchant": false,
        };
    }**/

    /**function createUser(){
        return $http({
            url:BACKEND_URL + '/api/users/',
            method: 'POST',
            data: $httpParamSerializerJQLike({
                "username": $scope.create.username,
                "email": $scope.create.email,
                "password": $scope.create.password,
                "first_name": $scope.create.firstName,
                "last_name": $scope.create.lastName,
                "is_staff": false,
                "uType": $scope.create.uType,
                "isMerchant": $scope.create.isMerchant === true ? true : false,
                "accounts": []
            }),
            headers: {
                "authorization": authService.getAuth(),
                "Content-Type" :"application/x-www-form-urlencoded"
            }
        });
    }**/

    function fetchAccounts() {
        getAccountsForUser().then(function successCallback(response) {
            $scope.accounts = response.data;
        }, function errorCallback(response) {
            toast('Error loading accounts');
        });
    }

    /**$scope.submit = function () {
        console.log($scope.create);
        createUser().then(function successCallback(response) {
            console.log(response.data);
            toast('Successfully created User');
            fetchUsers();
            delete $scope.create; //clear inputted data
        }, function errorCallback(response) {
            toast('Error loading users');
        });
    };

    $scope.setRandomData = function () {
        setRandomCreateData();
    };**/

    /**$scope.editUser = function (user) {
        console.log('editUser',user);
        $state.transitionTo('dashboard_admin_edit_user',{user:user});
    };

    $scope.userData = function (user) {
        console.log('userData',user);
        $state.transitionTo('dashboard_admin',{user:user});
    };

    $scope.deleteUser = function (user) {
        console.log('delete user: ', user);
        $http.delete(user.url,{
            headers: {
                "authorization": authService.getAuth(),
            }
        }).then(function success(){
            fetchUsers();
        }, function errorCallback(){
            toast('Failed to delete User');
        })
    };**/


    fetchAccounts();



}];

