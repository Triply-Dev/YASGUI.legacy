#!/bin/bash
#download all dbpedia ttl files (use ttl over nt because of proper encoding of ttl. Dbpedia ttl files are essential ntriple files with comments)
echo "downloading ttl files"
wget -r --no-parent --no-directories http://downloads.dbpedia.org/3.9/en/ --accept "*ttl.bz2"


#unzip them
echo "unzipping files"
bunzip2 *.bz2

rm -f predicates.txt
#get the predicate from the dump (in a very naive way). 
echo "retrieving predicates from ttl files (naively)"
ls *.ttl | xargs awk '{print $2}' | sed '/^[^<]/ d' | sed '/[^>]$/ d' | sed 's/^<\(.*\)>$/\1/' >> predicates.txt

rm -f classes.txt
echo "retrieving classes from ttl files (naively)"
ls *.ttl | xargs awk '{if ($2 == "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>") print $3}' | sed '/^[^<]/ d' | sed '/[^>]$/ d' | sed 's/^<\(.*\)>$/\1/' >> classes.txt

#remove ttl files
echo "removing old ttl files"
rm *.ttl 

echo "making predicate list unique"
sort -u predicates.txt -o predicates.txt


echo "done"
