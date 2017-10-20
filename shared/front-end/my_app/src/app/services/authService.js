module.exports = ['$sessionStorage', function($sessionStorage){
    return {
        getAuth: function(){
            return $sessionStorage.auth;
        },
        setAuth: function(nAuth){
            $sessionStorage.auth = nAuth;
        },
        getUserId: function(){ return $sessionStorage.userId; },
        setUserId: function(nUserId){ $sessionStorage.userId = nUserId;},

        getUser: function(){ return $sessionStorage.user; },
        setUser: function(nUser){ $sessionStorage.user = nUser;}
    };
}];