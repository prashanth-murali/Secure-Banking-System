module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike','$stateParams', '$sessionStorage','$state', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike, $stateParams, $sessionStorage, $state) {

    console.log('$stateParams.user', $stateParams.user);
    if($stateParams.user !== null){
        $sessionStorage.editUser = $stateParams.user;
    }

    console.log('$sessionStorage.editUser', $sessionStorage.editUser);
    if($sessionStorage.editUser === undefined){
        $state.transitionTo('dashboard_admin');
    }
    $scope.storage = $sessionStorage;

    console.log('dashboard Admin edit user here', $stateParams);
    
    function toast(msg) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position('bottom right')
                .hideDelay(3000)
        );
    }

    $scope.update = function() {
        console.log('update user: ', $scope.storage.editUser);
        $http({
            url:$sessionStorage.editUser.url,
            method: "PUT",
            data:$sessionStorage.editUser,
            headers: {
                "authorization": authService.getAuth(),
            }
        });
    };

    $scope.setRandomData = function () {
        setRandomCreateData();
    };

}];

