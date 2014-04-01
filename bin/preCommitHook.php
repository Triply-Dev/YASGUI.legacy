#!/usr/bin/php
<?php

$commitFiles = getCommitFiles();

// most important thing: is our config parsable, and are sensitive things such as api keys excluded?
$succes = checkConfigFile($commitFiles);

$returnVal = 0;
if (!$succes) {
	echo "Invalid commit, stopping now\n";
	$returnVal = 1;
}
//exit(1);//DEBUG!!!!!!!!!!!!!!!!!!
exit ( $returnVal ); // 0: succes, 1, otherwise


function getCommitFiles() {
	$commitFiles = [];
	$lines = explode("\n", `git diff --cached --name-status`);
	foreach ($lines AS $line) {
		$cols = explode("\t", $line);
		$file = end($cols);
		if (strlen($file)) {
			$commitFiles[] = $file;
		}
	}
	return $commitFiles;
}

function checkConfigFile($commitFiles) {
	$succes = true;
	$configFile = "config/clientSettings.js";
	if (!file_exists($configFile)) {
		echo "Something wrong in our pre-commit hook. We want to check the config file ".$configFile." but we can't find it! Renamed perhaps? Cancelling commit";
		return false;
	}
	if (in_array($configFile, $commitFiles)) {
		//we only want the bracketed stuff
		$configString = file_get_contents($configFile);

		if (!checkFieldEmpty ( $configString, "trackingId" )) {
			echo "The tracking id is still in the config file, or this key is not specified at all.\n";
			return false;
		}
	}
	
	return $succes;
}
function checkFieldEmpty($configString, $key) {
	$keyFound = preg_match('/\s*["\']'.$key.'["\']\s*:\s*(.*)/', $configString, $matches);
	if (!$keyFound) return false;
	$value = $matches[1];
	if (strpos($value, '""') === 0 || stripos($value, "null")) return true;
	return false;
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
	
	
