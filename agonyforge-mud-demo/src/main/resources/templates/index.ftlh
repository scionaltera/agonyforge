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
            <p>You are logged in.
            </p>
            <form action="<@spring.url '/play' />" method="get">
                <button type="submit" class="btn btn-outline-primary">Play</button>
            </form>
            <form action="<@spring.url '/logout' />" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-outline-danger">Log Out</button>
            </form>
            </@secure>
            <@anonymous "OIDC_USER">
            <p>You are not logged in.</p>
            <form action="<@spring.url '/oauth2/authorization/cognito' />" method="get">
                <button type="submit" class="btn btn-outline-primary">Play</button>
            </form>
            </@anonymous>
        </section>
    </section>
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
