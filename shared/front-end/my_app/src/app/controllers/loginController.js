module.exports = ['$scope','$http','$mdToast', function($scope, $http, $mdToast) {
    console.log('controller here');

    $scope.greeting = 'Hola!';

    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    $scope.onLoginSubmit = function(){
        console.log('onLoginSubmit', $scope);


        $http.post(BACKEND_URL+'/api-token-auth/', {
            username: $scope.email,
            password: $scope.password
        },{
            headers:{
                "Content-Type":"application/json"
            }
        }).then(function successCallback(response) {
            toast('JWT token: ' + response.data);
        }, function errorCallback(response) {
            toast('Invalid Username or Password');
        });
    };
}];