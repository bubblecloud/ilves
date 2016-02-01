/*
(function() {
    var dynamicScriptElement = document.createElement('script'); dynamicScriptElement.type = 'text/javascript'; dynamicScriptElement.async = true;
    dynamicScriptElement.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var firstScriptElement = document.getElementsByTagName('script')[0]; firstScriptElement.parentNode.insertBefore(dynamicScriptElement, firstScriptElement);
})();
*/

window.org_bubblecloud_ilves_security_U2fConnector = function() {

    var connector = this;

    this.register = function(requestJson) {
        var request = JSON.parse(requestJson);
        u2f.register(request.registerRequests, request.authenticateRequests, function(data) {
            connector.onRegisterResponse(JSON.stringify(data), data.errorCode);
        });
    }

    this.authenticate = function(requestJson) {
        var request = JSON.parse(requestJson);
        //alert(JSON.stringify(request));
        u2f.sign(request.authenticateRequests, function(data) {
            connector.onAuthenticateResponse(JSON.stringify(data), data.errorCode);
        });
    }
}