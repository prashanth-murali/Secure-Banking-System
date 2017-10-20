module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike) {
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

    $scope.submit = function () {
        console.log($scope.create);
        createUser().then(function successCallback(response) {
            console.log(response.data);
        }, function errorCallback(response) {
            toast('Error loading users');
        });
    };

    getUsers().then(function successCallback(response) {
        $scope.users = response.data;
    }, function errorCallback(response) {
        toast('Error loading users');
    });

    setRandomCreateData();

}];
