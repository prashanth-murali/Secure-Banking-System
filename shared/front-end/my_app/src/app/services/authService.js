module.exports = function(){
    var auth;
    return {
        getAuth: function(){
            return auth;
        },
        setAuth: function(nAuth){
            console.log('setting auth: ', nAuth);
            auth = nAuth;
        }
    };
};