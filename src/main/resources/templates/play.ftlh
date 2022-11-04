<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#assign title="Play">
<#assign styles = [ '/css/play.css', '/css/color.css' ]>
<#include "inc/header.inc.ftlh">
<body>
<@secure "ROLE_USER">
    <section id="output-box">
        <ul id="output-list"></ul>
    </section>

    <section id="input-box">
        <form id="input-form">
            <input type="text" id="input" autocomplete="off" autofocus>
        </form>
    </section>
</@secure>
<@anonymous "ROLE_USER">
    <main class="container-fluid">
        <a href="<@spring.url '/oauth2/authorization/cognito' />">Log In</a>
    </main>
</@anonymous>
<@secure "ROLE_USER">
    <#assign scripts = [
    '/webjars/sockjs-client/dist/sockjs.min.js',
    '/webjars/webstomp-client/dist/webstomp.min.js',
    '/js/client.js'
    ]>
</@secure>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
