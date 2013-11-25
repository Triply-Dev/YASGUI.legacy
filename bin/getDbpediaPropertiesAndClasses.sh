#!/bin/bash
#download all dbpedia ttl files (use ttl over nt because of proper encoding of ttl. Dbpedia ttl files are essential ntriple files with comments)
echo "downloading ttl files"
wget -r --no-parent --no-directories http://downloads.dbpedia.org/3.9/en/ --accept "*ttl.bz2"

#unzip them
echo "unzipping files"
bunzip2 *.bz2

rm -f predicates.txt;
rm -f classes.txt;
#loop through files
for f in *.ttl; do 
	echo "parsing file $f";
	exit;
	#loop through lines
	while read line; do
	  #echo line | get 2nd column | remove non-uris | remove non-uris | remove < and >
	  predicate=`echo $line | awk '{print $2}' | sed '/^[^<]/ d' | sed '/[^>]$/ d' | sed 's/^<\(.*\)>$/\1/'`;
	  
	  if [ -n "$predicate" ]; then
		echo "$predicate" >> predicates.txt;
		if [[ "$predicate" = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type" ]]; then
			class=`echo $line | awk '{print $3}' | sed '/^[^<]/ d' | sed '/[^>]$/ d' | sed 's/^<\(.*\)>$/\1/'`;
			if [ -n "$class" ]; then
				echo "$class" >> classes.txt
			fi
		fi
	  fi
	done < $f
done

#remove ttl files
echo "removing old ttl files"
rm *.ttl 

#make the list unique (use unique->sort->unique. this is more efficient, as there more or less a natural ordering of these predicate)
echo "making predicate/class list unique"
sort predicates.txt | uniq > predicatesUnique.txt
mv predicatesUnique.txt predicates.txt
sort classes.txt | uniq > classesUnique.txt
mv classesUnique.txt classes.txt

echo "done"
