#!/usr/bin/php
<?php

//most important thing: is our config parsable, and are sensitive things such as api keys excluded?
$succes = checkConfigFile();

if (!$succes) {
	echo "Invalid commit, stopping now\n";
}
exit((int)!$succes); //0: succes, 1, otherwise




function checkConfigFile() {
	$succes = true;
	$configFile = "/home/lrd900/code/yasgui/hookConfig.ini";
	if (file_exists($configFile)) {
		$ini = parse_ini_file($configFile, true);
		if (!$ini) {
			echo "Unable to decode ini config file\n";
			return false;
		} else {
			if (!array_key_exists("dev", $ini)) {
				echo "No config found for dev deployment\n";
				return false;
			}
			if (!array_key_exists("master", $ini)) {
				echo "No config found for master deployment\n";
				return false;
			}
			$succes = checkArray($ini["dev"]);
			if ($succes) { //only need to check new one if previous check didnt find error
				$succes = checkArray($ini["master"]);
			}

		}
	} else {
		echo "Config file not found\n";
		return false;
	}
	
	return $succes;
}

function arrayKeyFilled($array, $key) {
	return (array_key_exists($key, $array) && strlen($array[$key]) > 0);
}
function checkArray($array) {
	$succes = true;
	if (arrayKeyFilled($array, "bitlyApiKey")) {
		echo "The bitly api key is still in the config file.\n";
		return false;
	}
	if (arrayKeyFilled($array, "bitlyUsername")) {
		echo "The bitly username is still in the config file.\n";
		return false;
	}
	if (arrayKeyFilled($array, "googleAnalyticsId")) {
		echo "The google analytics id is still in the config file.\n";
		return false;
	}
	return $succes;
}
	
	
