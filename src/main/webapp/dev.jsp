<%@ page import="com.data2semantics.yasgui.server.fetchers.ConfigFetcher,org.apache.commons.lang3.StringEscapeUtils,com.data2semantics.yasgui.shared.StaticConfig" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
<head>
<link href='http://fonts.googleapis.com/css?family=Audiowide' rel='stylesheet' type='text/css'>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="description" content="A user-friendly interface to query any remote SPARQL endpoint">
<title>YASGUI</title>
<link rel="shortcut icon" type="image/png" href="images/rdf.png">

<script src="assets/jquery-1.10.0.min.js"></script>
<script src="assets/jquery.browser.min.js"></script>
<script src="assets/jquery.history.js"></script>
<script src="assets/jquery.csv-0.71.js"></script>

<!-- qtip lib -->
<script src="assets/jquery.qtip.min.js"></script>
<link rel="stylesheet" href="assets/jquery.qtip.css">

<!-- Main codemirror stuff -->
<script src="assets/codemirror/codemirror.js"></script>
<link rel="stylesheet" href="assets/codemirror/codemirror.css">
<link rel="stylesheet" href="assets/codemirror/theme/yasgui.css">
<script src="assets/codemirror/mode/javascript/javascript.js"></script>
<script src="assets/codemirror/mode/xml/xml.js"></script>
<script src="assets/codemirror/mode/turtle/turtle.js"></script>
<!-- Autocompletion code -->
<link rel="stylesheet" href="assets/codemirror/addon/hint/show-hint.css">
<script src="assets/codemirror/addon/hint/show-hint.js"></script>

<script src="assets/yasguiHint.js"></script>
<!-- Highlight words onclick code -->
<script src="assets/codemirror/addon/search/searchcursor.js"></script>
<script src="assets/codemirror/addon/search/match-highlighter.js"></script>
<script src="assets/codemirror/addon/edit/matchbrackets.js"></script>

<!-- Flint code for sparql mode -->
<script src="assets/flint/sparql.js"></script>
<link rel="stylesheet" href="assets/flint/sparql.css">
<!-- Code for adding custom keycombination/mouseclick commands to codemirror -->
<script src="assets/codemirrorCommands.js"></script>


<script src="assets/yasgui.js?<% out.print(StaticConfig.VERSION); %>"></script>
<script src="assets/trie.js"></script>
<link rel="stylesheet" href="assets/yasgui.css?<% out.print(StaticConfig.VERSION); %>">
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" src="Yasgui/Yasgui.nocache.js"></script>
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
