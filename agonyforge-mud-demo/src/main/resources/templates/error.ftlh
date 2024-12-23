<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en-US">
<#assign title="Error">
<#include "inc/header.inc.ftlh">
<body>
<main class="container-fluid">
    <#include "inc/logo.inc.ftlh">
    <section class="row">
        <section class="col-12 text-center">
            <p>${error.status}: ${error.error}</p>
        </section>
    </section>
    <#include "inc/footer.inc.ftlh">
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
