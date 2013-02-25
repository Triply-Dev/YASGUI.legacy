<%@ page import="com.data2semantics.yasgui.server.fetchers.ConfigFetcher,org.apache.commons.lang3.StringEscapeUtils,com.data2semantics.yasgui.shared.StaticConfig" %>
<%        
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setDateHeader("Expires", -1);
%>
<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="description" content="A user-friendly interface to query any remote SPARQL endpoint">
<title>YASGUI</title>
<link href='http://fonts.googleapis.com/css?family=Audiowide' rel='stylesheet' type='text/css'>
<link rel="shortcut icon" type="image/png" href="images/rdf.png">
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"></script>
<script src="static/yasgui.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="static/yasgui.css?<% out.print(StaticConfig.VERSION); %>">
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" src="Yasgui/Yasgui.nocache.js"></script>
<script type="text/javascript">defaults = "<% 
out.print(StringEscapeUtils.escapeEcmaScript(ConfigFetcher.getJson(request.getSession().getServletContext().getRealPath("/"))));
%>"</script>
</head>

<body>
	<!-- OPTIONAL: include this if you want history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
		style="position: absolute; width: 0; height: 0; border: 0"></iframe>
	<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
	<noscript>
		<div
			style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
			Your web browser must have JavaScript enabled in order for this
			application to display correctly.</div>
	</noscript>

</body>
</html>
