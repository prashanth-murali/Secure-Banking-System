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

    $scope.filterRequest=function(access)
    {
        if(access.request=='pending')
        {
            return true;

        }
        return false;
    };

    $scope.getRequests=function(){
        return $http.get(BACKEND_URL + '/api/users/requests', {
            headers: {
                "authorization": authService.getAuth()
            }
        });
    }

    $scope.fetchRequests=function(){
        $scope.getRequests().then(function successCallback(response) {
            $scope.requests = response.data;
        }, function errorCallback(response) {
            if(response.status!=200) {
                alert('Error loading user details');
            }
        });
    }

    $scope.requestManager=function(){
        return $http.put(BACKEND_URL + '/api/users/requests/'+authService.getUserId(), {
            "request": "pending"
        },{
            headers:{
                "authorization": authService.getAuth()
            }
        });
    }

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



    $scope.approveRequest=function(id){
        return $http.put(BACKEND_URL+'/api/users/requests/'+id,{
            "request" : "true"
        },{
            headers:{
                "authorization": authService.getAuth()
            }
        }).then(function success(){
            alert('Accepted Request');
        }, function errorCallback(){
            alert('Failed to Accept');
        })
    }

    $scope.declineRequest=function(id){
        return $http.put(BACKEND_URL+'/api/users/requests/'+id,{
            "request" : "false"
        },{
            headers:{
                "authorization": authService.getAuth()
            }
        }).then(function success(){
            alert('Declined Request');
        }, function errorCallback(){
            alert('Failed to Decline');
        })
    }


    $scope.fetchUserById(authService.getUserId());
    $scope.fetchRequests();
}];


