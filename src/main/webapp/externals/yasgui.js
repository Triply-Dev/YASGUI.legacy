/**
 * Author: Mark Wallace
 *
 * This function asynchronously issues a SPARQL query to a
 * SPARQL endpoint, and invokes the callback function with the JSON 
 * Format [1] results.
 *
 * Refs:
 * [1] http://www.w3.org/TR/sparql11-results-json/
 */
function sparqlQueryJson(queryStr, endpoint, callback) {
	$.ajax({
		url : endpoint,
		type : 'POST',
		dataType : 'text',//get as text, let gwt parse it to gwt json object
		data : {
			query : queryStr,
			format: 'application/sparql-results+json'
		},
		beforeSend : function(xhr) {
			//nothing
		},

		success : function(data) {
			callback(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR);
			alert(textStatus);
			alert(errorThrown);
		}
	});
};