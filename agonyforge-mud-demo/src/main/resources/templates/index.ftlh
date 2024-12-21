<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#include "inc/header.inc.ftlh">
<body>
<main class="container-fluid">
    <div id="logo" class="row">
        <div class="col-1"></div>
        <div class="col-10 text-center">
            <img src="img/agony-forge-logo.png" class="img-fluid" alt="ASCII Art logo reading 'AGONY FORGE'"/>
        </div>
        <div class="col-1"></div>
    </div>
    <section class="row">
        <section class="col-12 d-flex justify-content-center gap-3">
            <@anonymous "OAUTH2_USER">
            <form action="<@spring.url '/oauth2/authorization/github' />" method="get">
                <button type="submit" class="btn btn-lg btn-success">Log In</button>
            </form>
            </@anonymous>
            <@secure "OAUTH2_USER">
            <form action="<@spring.url '/play' />" method="get">
                <button type="submit" class="btn btn-lg btn-primary">Play</button>
            </form>
            <form action="<@spring.url '/logout' />" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-lg btn-danger">Log Out</button>
            </form>
            </@secure>
        </section>
    </section>
    <#include "inc/footer.inc.ftlh">
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
