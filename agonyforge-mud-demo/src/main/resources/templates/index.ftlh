<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#include "inc/header.inc.ftlh">
<body>
<main class="container-fluid">
    <section class="row">
        <header>INDEX</header>
    </section>
    <section class="row">
        <section class="col-12">
            <@secure "OIDC_USER">
            <p>You are authenticated.</p>
            <p><a href="<@spring.url '/play' />">Play</a></p>
            <p><a href="<@spring.url '/logout' />">Log Out</a></p>
            </@secure>
            <@anonymous "OIDC_USER">
            <p>You are anonymous.</p>
            <p><a href="<@spring.url '/oauth2/authorization/cognito' />">Log In</a></p>
            </@anonymous>
        </section>
    </section>
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
