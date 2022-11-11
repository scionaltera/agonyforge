<#-- Adapted from https://github.com/spring-projects/spring-security/issues/3275 -->
<#macro secure roleName>
    <#assign authorized = false>
    <#if SPRING_SECURITY_CONTEXT??>
        <#list SPRING_SECURITY_CONTEXT.authentication.authorities as authority>
            <#if authority == roleName>
                <#assign authorized = true>
            </#if>
        </#list>
        <#if authorized>
            <#nested>
        </#if>
    </#if>
</#macro>

<#macro anonymous roleName>
    <#assign authorized = false>
    <#if SPRING_SECURITY_CONTEXT??>
        <#list SPRING_SECURITY_CONTEXT.authentication.authorities as authority>
            <#if authority == roleName>
                <#assign authorized = true>
            </#if>
        </#list>
        <#if authorized == false>
            <#nested>
        </#if>
    <#else>
        <#nested>
    </#if>
</#macro>
