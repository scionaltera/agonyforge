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
            <@secure "ROLE_USER">
            <p>You are authenticated.</p>
            <a href="<@spring.url '/logout' />">Log Out</a>
            </@secure>
            <@anonymous "ROLE_USER">
            <p>You are anonymous.</p>
            <a href="<@spring.url '/oauth2/authorization/cognito' />">Log In</a>
            </@anonymous>
        </section>
    </section>
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
