#!/usr/bin/php
<?php

//most important thing: is our config parsable, and are sensitive things such as api keys excluded?





$succes = checkConfigFile();




if (!$succes) {
	echo "Invalid commit, stopping now\n";
}
exit(!$succes); //0: succes, 1, otherwise




function checkConfigFile() {
	return false;
}