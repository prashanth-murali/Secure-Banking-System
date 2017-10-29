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

    $scope.fetchUserById(authService.getUserId());

}];


