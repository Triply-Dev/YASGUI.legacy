<%@ page import="com.data2semantics.yasgui.server.fetchers.ConfigFetcher,org.apache.commons.lang3.StringEscapeUtils,com.data2semantics.yasgui.shared.StaticConfig" %>
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
<link rel="shortcut icon" type="image/png" href="images/rdf.png?<% out.print(StaticConfig.VERSION); %>">

<!-- yasgui specific fonts -->
<link rel="stylesheet" href="fonts/fonts.css?<% out.print(StaticConfig.VERSION); %>">

<script src="assets/jquery-1.10.0.min.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/jquery.browser.min.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/jquery.history.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/jquery.csv-0.71.js?<% out.print(StaticConfig.VERSION); %>"></script>

<!-- qtip lib -->
<script src="assets/jquery.qtip.min.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="assets/jquery.qtip.css?<% out.print(StaticConfig.VERSION); %>">

<!-- Main codemirror stuff -->
<script src="assets/codemirror/codemirror.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/codemirror/util/formatting.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="assets/codemirror/codemirror.css?<% out.print(StaticConfig.VERSION); %>">
<link rel="stylesheet" href="assets/codemirror/theme/yasgui.css?<% out.print(StaticConfig.VERSION); %>">
<script src="assets/codemirror/mode/javascript/javascript.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/codemirror/mode/xml/xml.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/codemirror/mode/turtle/turtle.js?<% out.print(StaticConfig.VERSION); %>"></script>
<!-- Autocompletion code -->
<link rel="stylesheet" href="assets/codemirror/addon/hint/show-hint.css?<% out.print(StaticConfig.VERSION); %>">
<script src="assets/codemirror/addon/hint/show-hint.js?<% out.print(StaticConfig.VERSION); %>"></script>

<script src="assets/yasguiHint.js?<% out.print(StaticConfig.VERSION); %>"></script>
<!-- Highlight words onclick code -->
<script src="assets/codemirror/addon/search/searchcursor.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/codemirror/addon/search/match-highlighter.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/codemirror/addon/edit/matchbrackets.js?<% out.print(StaticConfig.VERSION); %>"></script>

<!-- Code for efficiently looping through all tokens (used for removing comments) -->
<script src="assets/codemirror/addon/runmode/runmode.js?<% out.print(StaticConfig.VERSION); %>"></script>

<script src="assets/json2.js?<% out.print(StaticConfig.VERSION); %>"></script>

<!-- Flint code for sparql mode -->
<script src="assets/flint/sparql.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="assets/flint/sparql.css?<% out.print(StaticConfig.VERSION); %>">
<!-- Code for adding custom keycombination/mouseclick commands to codemirror -->
<script src="assets/codemirrorHelper.js?<% out.print(StaticConfig.VERSION); %>"></script>

<!-- notification library -->
<script src="assets/noty/jquery.noty.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/noty/layouts/bottomRight.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/noty/layouts/bottomLeft.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/noty/themes/default.js?<% out.print(StaticConfig.VERSION); %>"></script>


<script src="assets/yasgui.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/trie.js?<% out.print(StaticConfig.VERSION); %>"></script>
<link rel="stylesheet" href="assets/yasgui.css?<% out.print(StaticConfig.VERSION); %>">
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" src="Yasgui/Yasgui.nocache.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script type="text/javascript">defaults = "<% 
//relative results in /var/lib/tomcat7/config..
//getContextPath prefixes /yasgui
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
