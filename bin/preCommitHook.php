#!/usr/bin/php
<?php
// most important thing: is our config parsable, and are sensitive things such as api keys excluded?
$succes = checkConfigFile ();
if ($succes) {
	checkSeleniumFile();
}

if (!$succes) {
	echo "Invalid commit, stopping now\n";
}
exit ( ! $succes ); // 0: succes, 1, otherwise



function checkSeleniumFile() {
	$succes = true;
	$configFile = "/home/lrd900/code/yasgui/bin/selenium/selenium.properties";
	if (file_exists ($configFile)) {
		$props = parse_properties(file_get_contents($configFile));
		if (!$props) {
			echo "Unable to prase selenium properties file\n";
			return false;
		} else {
			foreach ($props as $key => $val) {
				if ($val !== false) {
					echo "Not all properties are empty!\n";
					echo $key." => ".$val."\n";
					return false;
				}
			}
		}
	} else {
		echo "Config file not found\n";
		return false;
	}
	
	return $succes;
}
function checkConfigFile() {
	$succes = true;
	$configFile = "/home/lrd900/code/yasgui/src/main/webapp/config/config.json";
	if (file_exists ( $configFile )) {
		$json = json_decode ( file_get_contents ( $configFile ), true );
		if (! $json) {
			echo "Unable to decode json config file\n";
			return false;
		} else {
			if (arrayKeyFilled ( $json, "bitlyApiKey" )) {
				echo "The bitly api key is still in the config file.\n";
				return false;
			}
			if (arrayKeyFilled ( $json, "bitlyUsername" )) {
				echo "The bitly username is still in the config file.\n";
				return false;
			}
			if (arrayKeyFilled ( $json, "googleAnalyticsId" )) {
				echo "The google analytics id is still in the config file.\n";
				return false;
			}
			if (arrayKeyFilled ( $json, "mysqlUsername" )) {
				echo "the mysql username is still in config file.\n";
				return false;
			}
			if (arrayKeyFilled ( $json, "mysqlPassword" )) {
				echo "the mysql password is still in config file.\n";
				return false;
			}
			if (arrayKeyFilled ( $json, "mysqlHost" )) {
				echo "the mysql host is still in config file.\n";
				return false;
			}
			if ($json ['singleEndpointMode']) {
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
	return (array_key_exists ( $key, $array ) && strlen ( $array [$key] ) > 0);
}
function parse_properties($txtProperties) {
	$result = array ();
	
	$lines = split ( "\n", $txtProperties );
	$key = "";
	
	$isWaitingOtherLine = false;
	foreach ( $lines as $i => $line ) {
		
		if (empty ( $line ) || (! $isWaitingOtherLine && strpos ( $line, "#" ) === 0))
			continue;
		
		if (! $isWaitingOtherLine) {
			$key = substr ( $line, 0, strpos ( $line, '=' ) );
			$value = substr ( $line, strpos ( $line, '=' ) + 1, strlen ( $line ) );
		} else {
			$value .= $line;
		}
		
		/* Check if ends with single '\' */
		if (strrpos ( $value, "\\" ) === strlen ( $value ) - strlen ( "\\" )) {
			$value = substr ( $value, 0, strlen ( $value ) - 1 ) . "\n";
			$isWaitingOtherLine = true;
		} else {
			$isWaitingOtherLine = false;
		}
		
		$result [$key] = $value;
		unset ( $lines [$i] );
	}
	
	return $result;
}
	
	
