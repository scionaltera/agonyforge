<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#assign title="Error">
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
        <section class="col-12 text-center">
            <p>${errorAttributes.status}: ${errorAttributes.error}</p>
        </section>
    </section>
    <#include "inc/footer.inc.ftlh">
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
