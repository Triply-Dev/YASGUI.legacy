#!/usr/bin/php
<?php

//are all json file parsable?
checkJsonConfigFiles();

//and more specifically, is our example file there, and parsable?
checkJsonConfigFile("/home/lrd900/code/yasgui/config.json.example");
exit(0); //0: succes, 1, otherwise

function exitNow() {
	echo "Invalid commit, stopping now\n";
	exit(1);
}
function checkJsonConfigFiles() {
	chdir(__DIR__."/../");
	$jsonFiles = glob("*.json*");
	foreach ($jsonFiles as $jsonFile) {
		checkJsonConfigFile($jsonFile);
	}
}

function checkJsonConfigFile($file) {
	if (!file_exists($file)) {
		echo "Config file ".$file." does not exist";
		exitNow();
	}
	$jsonArray = json_decode(file_get_contents($file), true);
	if ($jsonArray == null) {
		echo "Unable to decode json file ".$file."\n";
		exitNow();
	}
}


	
	
