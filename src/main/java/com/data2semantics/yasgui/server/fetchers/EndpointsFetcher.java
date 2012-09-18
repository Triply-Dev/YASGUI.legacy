package com.data2semantics.yasgui.server.fetchers;

public class EndpointsFetcher {
	//can also use http://labs.mondeca.com/sparqlEndpointsStatus/endpoint/endpoint.html to check for availability
//	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
//		PREFIX dcat: <http://www.w3.org/ns/dcat#>
//		PREFIX dcterms: <http://purl.org/dc/terms/>
//
//
//		SELECT DISTINCT ?dataset ?datasetTitle ?datasetDescription ?endpoint  {
//		  ?dataset dcat:distribution ?distribution.
//		?distribution dcterms:format ?format.
//		?format rdf:value 'api/sparql'.
//		?distribution dcat:accessURL ?endpoint.
//		?dataset dcterms:title ?datasetTitle;
//			dcterms:description ?datasetDescription.
//		} LIMIT 50
}
