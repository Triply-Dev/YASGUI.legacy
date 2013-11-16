#!/usr/bin/php
<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
require_once 'Console/CommandLine.php';


$parser = new Console_CommandLine();
$parser->description = 'Store a file with predicates in the YASGUI database';
$parser->addOption('configFile', array(
		'short_name'  => '-c',
		'long_name'   => '--config',
		'description' => 'Location of YASGUI options file (make sure the database settings are filled in)',
		'help_name'   => 'CONFIG_FILE',
));
$parser->addOption('predicatesFile', array(
		'short_name'  => '-p',
		'long_name'   => '--predFile',
		'description' => "Location of predicates file (each line contains 1 uri)",
		'help_name'   => 'PREDICATES_FILE',
));
try {
	$options = $parser->parse();
	validateOptions($options->options);
	// do something with the result object
	storePredicates($options->options);
	//print_r($options->args);
} catch (Exception $exc) {
	$parser->displayError($exc->getMessage());
}

function validateOptions($options) {
	if ($options['configFile'] == null) {
		echo "No configFile passed as argument. Type ".basename(__FILE__)." --help for more info\n";
		exit;
	}
	if ($options['predicatesFile'] == null) {
		echo "No predicates passed as argument. Type ".basename(__FILE__)." --help for more info\n";
		exit;
	}
	if (!file_exists($options['predicatesFile'])) {
		echo "The predicates file you specified (".$options['predicatesFile'].") does not exist\n";
		exit;
	}
}

function storePredicates($options) {
	$yasguiConfig = null;
	if (!file_exists($options[configFile])) {
		echo "Config file ".$options[configFile]." not found. Exiting\n";
		exit;
	} else {
		$jsonString = file_get_contents($options[configFile]);
		$yasguiConfig = json_decode($jsonString,true);
		if ($yasguiConfig == null) {
			echo "Could not parse json in config file ".$options->configFile.". Exiting\n";
			exit;
		}
	}
	
	// Create connection
	$con = mysqli_connect ($yasguiConfig['mysqlHost'], $yasguiConfig['mysqlUsername'],$yasguiConfig['mysqlPassword'],$yasguiConfig['mysqlDb']);
	
	// Check connection
	if (mysqli_connect_errno ( $con )) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error ();
		exit;
	}
	
	$method = askMethod();
	$endpoint = askEndpoint();
	$purge = askPurgeOtherResults();
	$proceed = printSummary($options, $yasguiConfig, $method, $endpoint, $purge);
	if (!$proceed) {
		echo "Stopping\n";
		exit;
	}
	
	if ($purge) {
		purgeProperties($con, $endpoint, $method);
	}
	if (checkStore($options['predicatesFile'])) {
		doStore($con, $endpoint, $method, $options['predicatesFile']);
		setSuccessfulStoreLog($con, $endpoint, $method);
	}
	
	$currentMethodFlagged = getCurrentUpdateFlag($con, $endpoint);
	$askedMethodFlagged = askSetFlag($currentMethodFlagged);
	if ($askedMethodFlagged != $currentMethodFlagged) {
		changeUpdateFlag($con, $endpoint, $method, $askedMethodFlagged);
	} else {
		echo "\nwe did not change the flag setting\n";
	}
	mysqli_close($con);
}
function setSuccessfulStoreLog($con, $endpoint, $method) {
	$sqlQuery = "INSERT INTO LogPropertyFetcher (Endpoint, Status) VALUES ('".mysqli_real_escape_string($con, $endpoint)."', '".mysqli_real_escape_string($con, "successful")."')";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to change our properties log table. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
}

function changeUpdateFlag($con, $endpoint, $method, $methodFlagged) {
	//first clear settings for this endpoint
	
	$sqlQuery = "DELETE FROM DisabledPropertyEndpoints WHERE Endpoint = '".mysqli_real_escape_string($con, $endpoint)."'";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to reset disable property flags. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	if ($methodFlagged == "both") {
		$sqlQuery = "INSERT INTO DisabledPropertyEndpoints (Endpoint, Method) VALUES ('".mysqli_real_escape_string($con, $endpoint)."', 'property'), ('".mysqli_real_escape_string($con, $endpoint)."', 'lazy')";
	} else if ($methodFlagged != "none") {
		$sqlQuery = "INSERT INTO DisabledPropertyEndpoints (Endpoint, Method) VALUES ('".mysqli_real_escape_string($con, $endpoint)."', '".mysqli_real_escape_string($con, $methodFlagged)."')";
	} 
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to set no-update flag(s). exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	echo "No-update flag(s) for endpoint ".$endpoint." set.\n";
}


function getCurrentUpdateFlag($con, $endpoint) {
	$methodsFlagged = "none";
	$sqlQuery = "SELECT * FROM DisabledPropertyEndpoints WHERE Endpoint = '".mysqli_real_escape_string($con, $endpoint)."'";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "failed fetching the current update flag status from db. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	} else {
		if ($result->num_rows == 2) {
			$methodsFlagged = "both";
		} else {
			$row = $result->fetch_assoc();
			$methodsFlagged = $row['Method'];
		}
	}
	return $methodsFlagged;
}
function askSetFlag($currentMethodFlagged) {
	$optionsArray = [
		1 => "lazy",
		2 => "property",
		3 => "both",
		4 => "none"
	];
	$selectedOption = array_search($currentMethodFlagged, $optionsArray);
	echo "\nWe've now stored the properties. Do you want to set a flag in our database which stops YASGUI from adding more properties automatically?\n".
		"Do this when you are sure you've added all possible properties to the database.\n".
		"	[1".($selectedOption == 1? "*": "")."]: Disable automatically fetching from queries executed on this endpoint\n".
		"	[2".($selectedOption == 2? "*": "")."]: Disable querying the dataset for rdf:properties\n".
		"	[3".($selectedOption == 3? "*": "")."]: Disable both\n".
		"	[4".($selectedOption == 4? "*": "")."]: Disable none\n";
			
	$handle = fopen ("php://stdin","r");
	$option = strtolower(trim(fgets($handle)));
	if(array_key_exists($option, $optionsArray)) {
		return $optionsArray[$option];
	} else {
		echo "Invalid input\n\n";
		return askSetFlag($currentFlagSet);
	}
}

function purgeProperties($con, $endpoint, $method) {
	$query="DELETE FROM Properties WHERE Endpoint='".mysqli_real_escape_string($con, $endpoint)."' AND Method='".mysqli_real_escape_string($con, $method)."'";
	$result=mysqli_query($con, $query);
	if (!$result) {
		echo "Failed to purge results. Exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	echo "\nPurged properties\n";
}

function checkStore($predicatesFile) {
	echo "\nA sample of what we will add to the database is shown below (each line is inserted separately). Is this correct? [Y/n]\n\n";
	$fh = fopen($predicatesFile, 'r');
	$count = 0;
	while ($property = fgets($fh)) {
		echo $property;
		$count++;
		if ($count == 10) {
			break;
		}
	}
	
	$handle = fopen ("php://stdin","r");
	$correct = strtolower(trim(fgets($handle)));
	$optionsArray = [
		"y" => true,
		"n" => false
	];
	if(array_key_exists($correct, $optionsArray)) {
		return $optionsArray[$correct];
	} else if (strlen($correct) == 0) {
		return true;
	} else {
		echo "Invalid input\n\n";
		return checkStore($predicatesFile);
	}
}
function doStore($con, $endpoint, $method, $predicatesFile) {
	echo "Storing properties\n";
	$fh = fopen($predicatesFile, 'r');
	$count = 0;
	$values = [];
	while ($property = fgets($fh)) {
		if (strlen($property) > 0) {
			$values[] = '("'.mysqli_real_escape_string($con, trim($property)).'", "'.mysqli_real_escape_string($con, $endpoint).'","'.mysqli_real_escape_string($con, $method).'")';
			$count++;
		} 
		if ($count % 1000 == 0) {
			$sqlQuery = "INSERT INTO Properties (Uri, Endpoint, Method) VALUES ".implode(',', $values);
			$values = [];
			$result=mysqli_query($con, $sqlQuery);
			if (!$result) {
				echo "Failed to store properties. Exiting\n";
				echo mysqli_error ($con)."\n";
				exit;
			} else {
				echo $count." stored\n";
			}
		}
	}
	$sqlQuery = "INSERT INTO Properties (Uri, Endpoint, Method) VALUES ".implode(',', $values);
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to store properties. Exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	} else {
		echo $count." stored\n";
	}
	echo "Stored all properties (".$count.")\n";
}

function askMethod() {
	$optionsArray = [
		1 => "lazy",
		2 => "property"
	];
	echo "\nFor which method do you want to import these predicates? \n".
		"[1] cached from queries (in practice more reliable than [2])\n".
		"[2] retrieved via querying for rdf:property\n";
	$handle = fopen ("php://stdin","r");
	$chosenMethod = intval(trim(fgets($handle)));
	if($chosenMethod != 1 && $chosenMethod != 2){
		echo "Wrong input. Choose either 1 or 2\n\n";
		return askMethod();
	} else {
		return $optionsArray[$chosenMethod];
	}
}

function askEndpoint() {
	echo "\nFor which endpoint do you want to import these predicates? \n";
	$handle = fopen ("php://stdin","r");
	$endpoint = trim(fgets($handle));
	if(strlen($endpoint) == 0){
		echo "No input provided\n\n";
		return askEndpoint();
	} else {
		return $endpoint;
	}
}

function askPurgeOtherResults() {
	$optionsArray = [
		"y" => true,
		"n" => false
	];
	echo "\nFor the endpoint and method you specified, do you want to delete (purge) all existing properties in the database? [Y/n]\n";
	$handle = fopen ("php://stdin","r");
	$purgeString = strtolower(trim(fgets($handle)));
	if(array_key_exists($purgeString, $optionsArray)) {
		return $optionsArray[$purgeString];
	} else if (strlen($purgeString) == 0) {
		return true;
	} else {
		echo "Invalid input\n\n";
		return askPurgeOtherResults();
	}
}

function printSummary($options, $yasguiConfig, $method, $endpoint, $purge) {
	echo "\nBefore proceeding, are the following settings correct?\n";
	echo "We will import the predicates from file ".$options['predicatesFile'].", for endpoint ".$endpoint." and method ".$method.".\n".
			"All other properties in our database for this endpoint and method will ".($purge?"":"not ")."be deleted.\n".
			"We'll perform these operations in database ".$yasguiConfig['mysqlDb']."\n".
			"Is this correct? (Y/n)\n";
	$optionsArray = [
		"y" => true,
		"n" => false
	];
	$handle = fopen ("php://stdin","r");
	$correct = strtolower(trim(fgets($handle)));
	if(array_key_exists($correct, $optionsArray)) {
		return $optionsArray[$correct];
	} else if (strlen($correct) == 0) {
		return true;
	} else {
		echo "Invalid input\n\n";
		return printSummary($options, $method, $endpoint, $purge);
	}
}



