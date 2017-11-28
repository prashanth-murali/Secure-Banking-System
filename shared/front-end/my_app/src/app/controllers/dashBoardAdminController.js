module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', '$state', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike, $state) {
    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    $scope.filterAdmin=function(user)
    {
        if(user.type=='tier1' || user.type=='tier2')
        {
            return true;

        }
        return false;
    };


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

    $scope.update = function(address,email,id,name,password,phoneNumber,type,username){
        return $http.put(BACKEND_URL+'/api/users/'+id,{
            "address":address,
            "id": id,
            "name": name,
            "phoneNumber":phoneNumber,
            "type":type
        },{
            headers:{
                "authorization": authService.getAuth()
            }
        }).then(function success(){
            alert('Successfully updated');
        }, function errorCallback(){
            alert('Failed to update User');
        })
    };

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

    $scope.deleteUser = function(userId){
        $http({
            url:BACKEND_URL + '/api/users/'+userId,
            method: 'DELETE',
            headers: {
                "authorization": authService.getAuth(),
            }
        });
        $scope.selectedAccount = null; // deselect user
        fetchUsers();
    };

    function createUser(){
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
    }

    function fetchUsers() {
        getUsers().then(function successCallback(response) {
            $scope.users = response.data;
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

    fetchUsers();

}];


