module.exports = function(){
    var jwt;
    return {
        getJwt: function(){
            return jwt;
        },
        setJwt: function(nJwt){
            console.log('setting jwt: ', nJwt);
            jwt = nJwt;
        }
    };
};