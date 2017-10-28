module.exports = ['$scope', '$http', 'authService', '$mdToast', '$httpParamSerializerJQLike', '$state', function ($scope, $http, authService, $mdToast, $httpParamSerializerJQLike, $state) {

    var fetchLog = function(){
        $http({
            url:BACKEND_URL + '/api/log',
            method: 'GET',
            headers: {
                "authorization": authService.getAuth(),
            }
        }).then(function(res){
            console.log(res);
           $scope.logFileContent = res.data.logFileContent;
        });
    };

    fetchLog();

}];


