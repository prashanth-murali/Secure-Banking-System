module.exports = ['$scope','$http','$mdToast', 'authService', '$state', '$base64', function($scope, $http, $mdToast, authService, $state, $base64) {
    console.log('controller here');

    $scope.email = 'admin_pall';
    $scope.password = 'admin';

    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    function login(auth){
        return $http.post(BACKEND_URL+'/api/login/',{
            "username": "admin_pall",
            "password": "admin"
        },{
            headers:{
                "authorization": auth
            }
        });
    }

    /**
     * @typedef {Object} User
     * @property {string} url The hateos link to the user resource
     * @property {string} username User's Username
     * @property {string} email User's email
     * @property {string} first_name User's first name
     * @property {string} last_name User's last name
     * @property {boolean} is_staff if the user can login to Django Admin
     * @property {string} Type User role type
     * @property {Array.<string[]>} accounts account list
     * @property {boolean} isMerchant
     */

    /**
     * @return {Promise}
     */
    function getUser(){
        return $http.get(BACKEND_URL + '/api/users/' + authService.getUserId() + '/', {
            headers: {
                "authorization": authService.getAuth()
            }
        });
    }

    function routeBasedOnUserRole(user) {
        if (user.type === 'tier1') {
            //todo
        }
        else if (user.type === 'tier2') {
            //todo
        }
        else if (user.type === 'administrator') {
            $state.transitionTo('dashboard_admin');
        }
        else if (user.type === 'merchant') {
            $state.transitionTo('dashboard_external_user');
        }
        else if (user.type === 'consumer') {
            $state.transitionTo('dashboard_external_user');
        }
        else {
            toast('Invalid user type');
        }
    }

    $scope.onLoginSubmit = function(){
        var auth = "Basic " +$base64.encode($scope.email + ":" + $scope.password);
        console.log('onLoginSubmit', $scope);
        console.log(auth);
        login(auth).then(function successCallback(response) {
            toast('Success');
            authService.setAuth(auth);
            authService.setUserId(response.data.id);
        }, function errorCallback(response) {
            toast('Invalid Username or Password');
        }).then(getUser).then(function successCallback(response) {
            var user = response.data;
            authService.setUser(user);
            routeBasedOnUserRole(user);
        },function errorCallback(){
            toast('Error fetching user');
        });
    };
}];
