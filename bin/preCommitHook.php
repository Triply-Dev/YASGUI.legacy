#!/usr/bin/php
<?php

//most important thing: is our config parsable, and are sensitive things such as api keys excluded?
$succes = checkConfigFile();

if (!$succes) {
	echo "Invalid commit, stopping now\n";
}
exit(!$succes); //0: succes, 1, otherwise




function checkConfigFile() {
	$succes = true;
	$configFile = "/home/lrd900/code/yasgui/src/main/webapp/config/config.json";
	if (file_exists($configFile)) {
		$json = json_decode(file_get_contents($configFile), true);
		if (!$json) {
			echo "Unable to decode json config file\n";
			return false;
		} else {
			if (arrayKeyFilled($json, "bitlyApiKey")) {
				echo "The bitly api key is still in the config file.\n";
				return false;
			}
			if (arrayKeyFilled($json, "bitlyUsername")) {
				echo "The bitly username is still in the config file.\n";	
				return false;
			}
			if (arrayKeyFilled($json, "googleAnalyticsId")) {
				echo "The google analytics id is still in the config file.\n";		
				return false;
			}
			if ($json['singleEndpointMode']) {
				echo "Committing config in single endpoint mode! Shouldnt be the case\n";
				return false;
			}
		}
	} else {
		echo "Config file not found\n";
		return false;
	}
	
	return $succes;
}

function arrayKeyFilled($array, $key) {
	var_export($array);
	var_export($key);
	return (array_key_exists($key, $array) && strlen($array[$key]) > 0);
}
	
	
