<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en-US">
<#assign title="Error">
<#include "inc/header.inc.ftlh">
<body>
<main class="container-fluid">
    <section class="row">
        <header>ERROR</header>
    </section>
    <section class="row">
        <section class="col-12">
            <p>${errorAttributes.status}: ${errorAttributes.error}</p>
        </section>
    </section>
</main>
<#include "inc/scripts.inc.ftlh">
</body>
</html>
