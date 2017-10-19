module.exports = function(){
    var auth;
    var userId;
    var user;
    return {
        getAuth: function(){
            return auth;
        },
        setAuth: function(nAuth){
            console.log('setting auth: ', nAuth);
            auth = nAuth;
        },
        getUserId: function(){ return userId; },
        setUserId: function(nUserId){ userId = nUserId;},

        getUser: function(){ return user; },
        setUser: function(nUser){ user = nUser;}
    };
};