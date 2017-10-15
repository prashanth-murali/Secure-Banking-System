var http = require('http'),
    httpProxy = require('http-proxy');
//
// Create your proxy server and set the target in the options.
//
var proxy = httpProxy.createProxyServer({});

proxy.on('proxyRes', function(proxyRes, req, res) {
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader("Access-Control-Allow-Credentials", "true");
    res.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
    res.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    console.log('RAW Response from the target', JSON.stringify(proxyRes.headers, true, 2));
});

http.createServer(function(req, res) {
    proxy.web(req, res, {target:'http://localhost:8000'});
}).listen(9000); // See (†)

console.log('listenting on port 9000');