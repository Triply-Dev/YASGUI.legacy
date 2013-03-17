#!/usr/bin/php
<?php

//most important thing: is our config parsable, and are sensitive things such as api keys excluded?





$exit_status = checkConfigFile();




if ($exit_status) {
	echo "Invalid commit, stopping now\n";
}
exit((int)$exit_status); //0: succes, 1, otherwise




function checkConfigFile() {
	return false;
}