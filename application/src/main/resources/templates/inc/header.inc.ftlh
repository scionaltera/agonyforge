<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <#if title??>
        <title>${title} - The Agony Forge</title>
    <#else>
        <title>The Agony Forge</title>
    </#if>
    <link rel="stylesheet" href="<@spring.url 'https://fonts.googleapis.com/css?family=Inconsolata'/>">
    <link rel="stylesheet" href="<@spring.url '/webjars/bootstrap/css/bootstrap.min.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/style.css'/>">
    <#if styles??>
        <#list styles as style>
            <link rel="stylesheet" href="<@spring.url '${style}'/>">
        </#list>
    </#if>
</head>
