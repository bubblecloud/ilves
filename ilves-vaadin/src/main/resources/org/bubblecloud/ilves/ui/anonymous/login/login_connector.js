/*
(function() {
    var dynamicScriptElement = document.createElement('script'); dynamicScriptElement.type = 'text/javascript'; dynamicScriptElement.async = true;
    dynamicScriptElement.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var firstScriptElement = document.getElementsByTagName('script')[0]; firstScriptElement.parentNode.insertBefore(dynamicScriptElement, firstScriptElement);
})();
*/

window.org_bubblecloud_ilves_ui_anonymous_login_LoginConnector = function() {

    var connector = this;
    this.getCredentials = function() {
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;
        connector.onCredentials(username, password);
    }

    this.saveCredentials = function() {
        document.getElementById("loginForm").submit();
        connector.onSave();
    }

}