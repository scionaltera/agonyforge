<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <#if title??>
        <title>Agony Forge MUD - ${title}</title>
    <#else>
        <title>Agony Forge MUD</title>
    </#if>
    <link rel="icon" href="<@spring.url '/img/favicon.ico'/>">
    <link rel="stylesheet" href="<@spring.url '/webjars/bootstrap/css/bootstrap.min.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/style.css'/>">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono">
    <#if styles??>
        <#list styles as style>
            <link rel="stylesheet" href="<@spring.url '${style}'/>">
        </#list>
    </#if>
</head>
