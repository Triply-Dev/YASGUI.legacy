
clientSettings = {
	//"See github wiki page for more info: http://laurensrietveld.nl/yasgui/deployment.html"
	//"Getting exceptions after changing the json file? Make sure you have valid json here: http://jsonlint.com/",
	"constants": {
		"logging": {
			"type": "googleAnalytics",
			"trackingId": ""
		},
		"githubUsername": "",
		"githubOathToken": "",
		"githubRepo": "",
		"browserTitle": "YASGUI",
	},
	
	
	"defaults": {
		"tabularBrowsingTemplate": "SELECT ?property ?hasValue ?isValueOf\nWHERE {\t{ <URI> ?property ?hasValue\t}\nUNION\t{ ?isValueOf ?property <URI> }\n}",
		"showDownloadProgressBar": true,
		"loggingEnabled": true,
		"tabSettings":  {
			"endpoint": "http://dbpedia.org/sparql",
			"query": "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n\nSELECT * WHERE {\n  ?sub ?pred ?obj\n} LIMIT 10\n",
			"tabTitle": "Query",
			"contentTypeSelect": "application/sparql-results+xml",
			"contentTypeGraph": "text/turtle",
			"outputFormat": "table",
			"requestMethod": "POST",
			"params": [],//[{ name: "first", value: "Rick" }] (used to be 'extraArgs'
			"headers": [],
			"namedGraphs": [],
			"defaultGraphs": []
		}
	},
	"allowedFeatures": {
		"endpointSelection": true,
		"requestParameters": true,
		"requestHeaders": true,
		"namedGraphs": true,
		"defaultGraphs": true,
		"acceptHeaders": true,
		"requestMethod": true,
		"offlineCaching": true,
		"querySharing": true,
		"queryBookmarks": true,
		"propertyCompletionMethods": {
			"lov": true,
			"query": true,
			"queryResults": true
		},
		"classCompletionMethods": {
			"query": true,
			"queryResults": true,
			"lov": true
		}
	}
};
