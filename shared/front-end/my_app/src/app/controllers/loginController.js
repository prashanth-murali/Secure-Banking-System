module.exports = ['$scope','$http','$mdToast', 'authService', '$state', '$base64', function($scope, $http, $mdToast, authService, $state, $base64) {
    console.log('controller here');

    $scope.email = 'james.kieley';
    $scope.password = 'asdf1234';

    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    $scope.onLoginSubmit = function(){
        var auth = "Basic " +$base64.encode($scope.email + ":" + $scope.password);
        console.log('onLoginSubmit', $scope);
        console.log(auth);
        $http.get(BACKEND_URL+'/api/login/',{
            headers:{
                "authorization": auth
            }
        }).then(function successCallback(response) {
            toast('Success');
            authService.setAuth(auth);
            $state.transitionTo('my.state');
        }, function errorCallback(response) {
            toast('Invalid Username or Password');
        });
    };
}];
