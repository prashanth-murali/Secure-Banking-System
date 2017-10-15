module.exports = ['$scope','$http','$mdToast','$timeout', function($scope, $http, $mdToast, $timeout) {
    console.log('controller here');

    $timeout(function(){
        $mdToast.show(
            $mdToast.simple()
                .textContent('Simple Toast!')
                .position('bottom right')
                .hideDelay(3000)
        );

    }, 1000);


    $scope.greeting = 'Hola!';
    $scope.onLoginSubmit = function(){
        console.log('onLoginSubmit', $scope);


        $http({
            method: 'POST',
            url: BACKEND_URL+'/api-token-auth/',
            data:{
                username: $scope.email,
                password: $scope.password
            },
            headers:{
                "Content-Type":"application/json"
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