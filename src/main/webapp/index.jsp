<%@ page import="com.data2semantics.yasgui.server.fetchers.ConfigFetcher,org.apache.commons.lang3.StringEscapeUtils,com.data2semantics.yasgui.shared.StaticConfig" %>
<%        
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setDateHeader("Expires", -1);
%>
<!doctype html>

<!-- Important: add all included resourdces to gwt.xml file as well, so they are added to the appcache manifest file -->
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="description" content="A user-friendly interface to query any remote SPARQL endpoint">
<title><% 
String title = ConfigFetcher.getValueAsString(request.getSession().getServletContext().getRealPath("/"), "browserTitle");
if (title == null) {
	out.print("YASGUI");
} else {
	out.print(title);
}
%></title>

<link rel="shortcut icon" type="image/png" href="images/rdf.png?<% out.print(StaticConfig.VERSION); %>">
<script src="static/yasgui.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="static/yasgui.css?<% out.print(StaticConfig.VERSION); %>">
<!-- yasgui specific fonts -->
<link rel="stylesheet" href="fonts/fonts.css?<% out.print(StaticConfig.VERSION); %>">

<script type="text/javascript" src="Yasgui/Yasgui.nocache.js?<% out.print(StaticConfig.VERSION); %>"></script>
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
