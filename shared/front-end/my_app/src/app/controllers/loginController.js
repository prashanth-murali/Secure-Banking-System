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
            authService.setUserId(response.data.userId);

        }, function errorCallback(response) {
            toast('Invalid Username or Password');

        }).then(function() {
            return $http.get(BACKEND_URL + '/api/users/' + authService.getUserId() + '/', {
                headers: {
                    "authorization": authService.getAuth()
                }
            });
        }).then(function successCallback(response) {
            /*
            {
                "url": "http://localhost:8000/users/59e2d634ea4ca065d3011043/",
                "username": "james.kieley",
                "email": "jkieley@asu.edu",
                "first_name": "",
                "last_name": "",
                "is_staff": true,
                "uType": "",
                "accounts": [],
                "isMerchant": false
            }
             */
            var user = response.data;
            console.log(user);
            authService.setUser(user);

            if(user.uType === 'tier1'){
                //todo
            }
            else if(user.uType === 'tier2'){
                //todo
            }
            else if(user.uType === 'administrator'){
                $state.transitionTo('dashboard_admin');
            }
            else if(user.uType === 'external'){
                $state.transitionTo('dashboard_external_user');
            }
            else{
                toast('Invalid user uType');
            }
        },function errorCallback(){
            toast('Error fetching user');
        });
    };
}];
