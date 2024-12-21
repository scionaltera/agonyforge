<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#assign title="Play">
<#assign styles = [ '/css/play.css', '/css/color.css' ]>
<#include "inc/header.inc.ftlh">
<body>
<@secure "OAUTH2_USER">
    <section id="output-box">
        <ul id="output-list"></ul>
    </section>

    <section id="input-box">
        <form id="input-form">
            <input type="text" id="input" autocomplete="off" autofocus>
        </form>
    </section>
</@secure>
<@anonymous "OAUTH2_USER">
    <main class="container-fluid">
        <form action="<@spring.url '/oauth2/authorization/github' />" method="get">
            <button type="submit" class="btn btn-success">Log In</button>
        </form>
    </main>
</@anonymous>
<@secure "OAUTH2_USER">
    <#assign scripts = [
    '/webjars/sockjs-client/dist/sockjs.min.js',
    '/webjars/webstomp-client/dist/webstomp.min.js',
    '/js/client.js'
    ]>
</@secure>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
