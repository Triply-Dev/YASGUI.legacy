#!/usr/bin/php
<?php

//most important thing: is our config parsable, and are sensitive things such as api keys excluded?
checkConfigFile();
checkJsonConfigFiles();
exit(0); //0: succes, 1, otherwise

function exitNow() {
	echo "Invalid commit, stopping now\n";
	exit(1);
}
function checkJsonConfigFiles() {
	$jsonFiles = glob("*.json");
	foreach ($jsonFiles as $jsonFile) {
		checkJsonConfigFile($jsonFile);
	}
}

function checkJsonConfigFile($file) {
	$jsonArray = json_decode(file_get_contents($file), true);
	if ($jsonArray == null) {
		echo "Unable to decode json file ".$file."\n";
	}
	checkArray($jsonArray);
}

function checkConfigFile() {
	$configFile = "/home/lrd900/code/yasgui/hookConfig.ini";
	if (file_exists($configFile)) {
		$ini = parse_ini_file($configFile, true);
		if (!$ini) {
			echo "Unable to decode ini config file\n";
			exitNow();
		} else {
			if (!array_key_exists("dev", $ini)) {
				echo "No config found for dev deployment\n";
				exitNow();
			}
			if (!array_key_exists("master", $ini)) {
				echo "No config found for master deployment\n";
				exitNow();
			}
			foreach ($ini['mail'] as $key => $mailConfig) {
				if (strlen($mailConfig)) {
					echo "Setting ". $key. " in ini mail config is set to ".$mailConfig.". Leave this empty instead.\n";
					exitNow();
				}
			}

		}
	} else {
		echo "Config file not found\n";
		exitNow();
	}
}

function arrayKeyFilled($array, $key) {
	return (array_key_exists($key, $array) && strlen($array[$key]) > 0);
}
function checkArray($array) {
	$succes = true;
	if (arrayKeyFilled($array, "bitlyApiKey")) {
		echo "The bitly api key is still in the config file.\n";
		exitNow();
	}
	if (arrayKeyFilled($array, "bitlyUsername")) {
		echo "The bitly username is still in the config file.\n";
		exitNow();
	}
	if (arrayKeyFilled($array, "googleAnalyticsId")) {
		echo "The google analytics id is still in the config file.\n";
		exitNow();
	}
}
	
	
