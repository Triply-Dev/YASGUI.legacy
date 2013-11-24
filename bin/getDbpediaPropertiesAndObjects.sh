#!/bin/bash
#download all dbpedia ttl files (use ttl over nt because of proper encoding of ttl. Dbpedia ttl files are essential ntriple files with comments)
echo "downloading ttl files"
wget -r --no-parent --no-directories http://downloads.dbpedia.org/3.9/en/ --accept "*ttl.bz2"


#unzip them
echo "unzipping files"
bunzip2 *.bz2


#get the predicate from the dump (in a very naive way). 
echo "retrieving predicates from ttl files (naively)"
cat *.ttl | awk '{print $2}' >> predicates.txt

#remove ttl files
echo "removing old ttl files"
#rm *.ttl 

#make the list unique (use unique->sort->unique. this is more efficient, as there more or less a natural ordering of these predicate)
echo "making predicate list unique"
uniq predicates.txt | sort | uniq > predicatesUnique.txt
mv predicatesUnique.txt predicates.txt
#sort -u predicates.txt -o predicates.txt

#our awk opertation did not deal with comments, i.e. we might have some results in here which is just text from comments, and not uris.
#check whether every line is a uri, and delete others
echo "removing non-URIs from predicate list (e.g. comments from the ttl file)"
sed -i '/^[^<]/ d' predicates.txt
sed -i '/[^>]$/ d' predicates.txt

#remove < and >
echo "removing < and > from predicate list"
sed -i 's/^<\(.*\)>$/\1/' predicates.txt 

echo "done"