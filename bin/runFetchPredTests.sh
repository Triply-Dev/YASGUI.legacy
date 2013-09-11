#!/bin/bash
 
if [ -z "$1" ];then
	echo "need endpoint as arg to perform tests on"
	exit;
fi
endpoint="$1"
runName=""
if [ -n "$2" ];then
	runName="$2"
fi
numResultsFile="numResults.$2.txt"
timingsFile="timings.$2.txt"
echo "" > $numResultsFile
echo "" > $timingsFile





#predicate with order by
title="predicate with order by"
echo "$title"
echo "$title" >> $numResultsFile 
echo "$title" >> $timingsFile
start=$(date +%s)
curl  --data-urlencode "query=SELECT DISTINCT ?property WHERE {?x ?property ?z} ORDER BY ?property"  -X POST  --header "Accept:text/csv" "$endpoint" | wc -l >> $numResultsFile
end=$(date +%s)
diff=$(( $end - $start ))
echo "$diff sec" >> $timingsFile


title="predicate without order by"
echo "$title"
echo "$title" >> $numResultsFile 
echo "$title" >> $timingsFile
start=$(date +%s)
curl  --data-urlencode "query=SELECT DISTINCT ?property WHERE {?x ?property ?z}"  -X POST  --header "Accept:text/csv" "$endpoint" | wc -l >> $numResultsFile
end=$(date +%s)
diff=$(( $end - $start ))
echo "$diff sec" >> $timingsFile


title="property with order by"
echo "$title"
echo "$title" >> $numResultsFile 
echo "$title" >> $timingsFile
start=$(date +%s)
curl  --data-urlencode "query=SELECT DISTINCT ?pred WHERE { ?pred a rdf:Property} ORDER BY ?pred"  -X POST  --header "Accept:text/csv" "$endpoint" | wc -l >> $numResultsFile
end=$(date +%s)
diff=$(( $end - $start ))
echo "$diff sec" >> $timingsFile


title="property without order by"
echo "$title"
echo "$title" >> $numResultsFile 
echo "$title" >> $timingsFile
start=$(date +%s)
curl  --data-urlencode "query=SELECT DISTINCT ?pred WHERE { ?pred a rdf:Property}"  -X POST  --header "Accept:text/csv" "$endpoint" | wc -l >> $numResultsFile
end=$(date +%s)
diff=$(( $end - $start ))
echo "$diff sec" >> $timingsFile

