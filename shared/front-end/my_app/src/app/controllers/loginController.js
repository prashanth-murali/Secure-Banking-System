module.exports = ['$scope','$http','$mdToast', 'jwtService', '$state', '$base64', function($scope, $http, $mdToast, jwtService, $state, $base64) {
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
        console.log('onLoginSubmit', $scope);
        console.log($base64.encode($scope.email+":"+$scope.password));


        $http.get(BACKEND_URL+'/api/login/',{
            headers:{
                "authorization": "Basic " +$base64.encode($scope.email+":"+$scope.password)
            }
        }).then(function successCallback(response) {
            toast('Success');
            jwtService.setJwt(response.data.token);
            $state.transitionTo('my.state', {arg:'arg'});
        }, function errorCallback(response) {
            toast('Invalid Username or Password');
        });
    };
}];
