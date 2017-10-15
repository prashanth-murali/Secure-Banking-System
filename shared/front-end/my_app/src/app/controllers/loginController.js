module.exports = ['$scope','$http', function($scope, $http) {
    console.log('controller here');
    $scope.greeting = 'Hola!';
    $scope.onLoginSubmit = function(){
        console.log('onLoginSubmit', $scope);

        $http({
            method: 'POST',
            url: BACKEND_URL+'/api-token-auth/',
            data:{
                username: $scope.email,
                password: $scope.password
            }
        }).then(function successCallback(response) {
            // this callback will be called asynchronously
            // when the response is available
        }, function errorCallback(response) {
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });
    };
}];