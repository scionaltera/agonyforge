<script type="text/javascript" src="<@spring.url '/webjars/bootstrap/js/bootstrap.bundle.min.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/webjars/jquery/jquery.min.js'/>"></script>
<#if scripts??>
<#list scripts as script>
<script type="text/javascript" src="<@spring.url '${script}'/>"></script>
</#list>
</#if>

