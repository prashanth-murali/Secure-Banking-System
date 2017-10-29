module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', '$state', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike, $state) {
    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }



    function randomString() {
        return Math.floor(Math.random()*90000) + 10000 + "";
    }

    function getUsers() {
        return $http.get(BACKEND_URL + '/api/users/', {
            headers: {
                "authorization": authService.getAuth()
            }
        });
    }

    function setRandomCreateData() {
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
        if(transaction.status=='pending')
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



    $scope.postCreateUser = function(username,email,password,phNumber,name,address,type){
        return $http.post(BACKEND_URL+'/api/users/',{
            "type": type,
            "name": name,
            "address": address,
            "phoneNumber": phNumber,
            "username": username,
            "password": password,
            "email":email
        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.createUser=function(username,email,password,phNumber,name,address,type)
    {
        $scope.postCreateUser(username,email,password,phNumber,name,address,type);
        alert('Request sent. Check your Email.');

    }
    function fetchUsers() {
        getUsers().then(function successCallback(response) {
            $scope.users = response.data;
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

    $scope.postcreateAccount=function(userID,amount,accountType){

        {
            return $http.post(BACKEND_URL+'/api/accounts/',{
                "userId": userID,
                "amount": amount,
                "accountType": accountType
            },{
                headers:{
                    "authorization": authService.getAuth()
                }
            });
        }

    }

    $scope.createAccount=function(userID,amount,accountType){
        $scope.postcreateAccount(userID,amount,accountType);
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



    $scope.submit = function () {
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
    };

    $scope.editUser = function (user) {
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
    };

    fetchUsers();
    fetchTransactions();

}];


