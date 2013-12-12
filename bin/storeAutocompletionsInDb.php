#!/usr/bin/php
<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
require_once 'Console/CommandLine.php';

$parser = new Console_CommandLine();
$parser->description = 'Store a file with predicates and/or classes in the YASGUI database';
$parser->addOption('configFile', array(
		'short_name'  => '-o',
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
$parser->addOption('classesFile', array(
		'short_name'  => '-c',
		'long_name'   => '--classesFile',
		'description' => "Location of classes file (each line contains 1 uri)",
		'help_name'   => 'CLASSES_FILE',
));
try {
	$options = $parser->parse();
	$types = validateOptions($options->options);
	foreach ($types AS $type) {
		// do something with the result object
		store($options->options, $type);
	}
} catch (Exception $exc) {
	$parser->displayError($exc->getMessage());
}

function validateOptions($options) {
	$availableTypes = [
		"property" => [
			"plural" => "properties",
			"pluralCamelCase" => "Properties",
			"singular" => "property",
			"singularCamelCase" => "Property"
		],
		"class" => [
			"plural" => "classes",
			"pluralCamelCase" => "Classes",
			"singular" => "class",
			"singularCamelCase" => "Class"
		]
	];
	$storeTypes = [];
	if ($options['configFile'] == null) {
		echo "No configFile passed as argument. Type ".basename(__FILE__)." --help for more info\n";
		exit;
	}
	if ($options['predicatesFile'] == null && $options['classesFile'] == null) {
		echo "No predicates or classes passed as argument. Type ".basename(__FILE__)." --help for more info\n";
		exit;
	}
	if ($options['predicatesFile'] != null) {
		if (!file_exists($options['predicatesFile'])) {
			echo "The predicates file you specified (".$options['predicatesFile'].") does not exist\n";
			exit;
		} else {
			$availableTypes["property"]["file"] = $options['predicatesFile'];
			$storeTypes[] = $availableTypes["property"];
		}
	}
	if ($options['classesFile'] != null) {
		if (!file_exists($options['classesFile'])) {
			echo "The classes file you specified (".$options['classesFile'].") does not exist\n";
			exit;
		} else {
			$availableTypes["class"]["file"] = $options['classesFile'];
			$storeTypes[] = $availableTypes["class"];
		}
	}
	return $storeTypes;
}

function store($options, $type) {
	$yasguiConfig = null;
	if (!file_exists($options["configFile"])) {
		echo "Config file ".$options["configFile"]." not found. Exiting\n";
		exit;
	} else {
		$jsonString = file_get_contents($options["configFile"]);
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
	
	$method = askMethod($type);
	$endpoint = askEndpoint($type);
	$purge = askPurgeOtherResults($type);
	$proceed = printSummary($options, $yasguiConfig, $method, $endpoint, $purge, $type);
	if (!$proceed) {
		echo "Stopping\n";
		exit;
	}
	$endpointId = (int)getIdForEndpoint($con, $endpoint);
	if ($purge) {
		purge($con, $endpointId, $method, $type);
	}
	if (checkStore($type)) {
		doStore($con, $endpointId, $method, $type);
		setSuccessfulStoreLog($con, $endpointId, $method, $type);
	}
	
	$currentMethodFlagged = getCurrentUpdateFlag($con, $endpointId, $type);
	$askedMethodFlagged = askSetFlag($currentMethodFlagged, $type);
	if ($askedMethodFlagged != $currentMethodFlagged) {
		changeUpdateFlag($con, $endpointId, $method, $askedMethodFlagged, $type);
	} else {
		echo "\nwe did not change the flag setting\n";
	}
	mysqli_close($con);
}

function getIdForEndpoint($con, $endpoint) {
	$sqlQuery = "SELECT Id FROM CompletionEndpoints WHERE Endpoint = '".mysqli_real_escape_string($con, $endpoint)."'";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "failed fetching the endpoint id from db. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	} else {
		if ($result->num_rows == 1) {
			$row = $result->fetch_assoc();
			return $row['Id'];
		} else {
			//insert it!
			$sqlQuery = "INSERT INTO CompletionEndpoints (Endpoint) VALUES ('".mysqli_real_escape_string($con, $endpoint)."')";
			$result = mysqli_query($con, $sqlQuery);
			if (!$result) {
				echo "Failed to create endpoint id for our endpoint. exiting\n";
				echo mysqli_error ($con)."\n";
				exit;
			}
			return mysqli_insert_id($con);
		}
	}
}
function setSuccessfulStoreLog($con, $endpointId, $method, $type) {
	$sqlQuery = "INSERT INTO CompletionsLog (EndpointId, Type, Status) VALUES (".$endpointId.", '".mysqli_real_escape_string($con, $type['singular'])."', '".mysqli_real_escape_string($con, "successful")."')";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to change our " . $type['plural'] .  " log table. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
}

function changeUpdateFlag($con, $endpointId, $method, $methodFlagged, $type) {
	//first clear settings for this endpoint
	
	$sqlQuery = "DELETE FROM DisabledCompletionEndpoints WHERE EndpointId = ".$endpointId." AND Type = '".mysqli_real_escape_string($con, $type['singular'])."'";
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to reset disable " . $type['singular'] .  " flags. exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	if ($methodFlagged == "both") {
		$sqlQuery = "INSERT INTO DisabledCompletionEndpoints (EndpointId, Type, Method) VALUES (".$endpointId.", '".mysqli_real_escape_string($con, $type['singular'])."', 'query'), (".$endpointId.", '".mysqli_real_escape_string($con, $type['singular'])."', 'queryResults')";
	} else if ($methodFlagged != "none") {
		$sqlQuery = "INSERT INTO DisabledCompletionEndpoints (EndpointId,Type,  Method) VALUES (".$endpointId.", '".mysqli_real_escape_string($con, $type['singular'])."', '".mysqli_real_escape_string($con, $methodFlagged)."')";
	} 
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to set no-update flag(s). exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	echo "No-update flag(s) for endpoint ".$endpoint." set.\n";
}


function getCurrentUpdateFlag($con, $endpointId, $type) {
	$methodsFlagged = "none";
	$sqlQuery = "SELECT * FROM DisabledCompletionEndpoints WHERE EndpointId = ".$endpointId." AND Type = '".mysqli_real_escape_string($con, $type['singular'])."'";
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
function askSetFlag($currentMethodFlagged, $type) {
	$optionsArray = [
		1 => "query",
		2 => "queryResults",
		3 => "both",
		4 => "none"
	];
	$selectedOption = array_search($currentMethodFlagged, $optionsArray);
	echo "\nWe've now stored the " . $type['plural'] .  ". Do you want to set a flag in our database which stops YASGUI from adding more " . $type['plural'] .  " automatically?\n".
		"Do this when you are sure you've added all possible " . $type['plural'] .  " to the database.\n".
		"	[1".($selectedOption == 1? "*": "")."]: Disable automatically fetching from queries executed on this endpoint\n".
		"	[2".($selectedOption == 2? "*": "")."]: Disable querying the dataset for " . $type['plural'] .  "\n".
		"	[3".($selectedOption == 3? "*": "")."]: Disable both\n".
		"	[4".($selectedOption == 4? "*": "")."]: Disable none\n";
			
	$handle = fopen ("php://stdin","r");
	$option = strtolower(trim(fgets($handle)));
	if(array_key_exists($option, $optionsArray)) {
		return $optionsArray[$option];
	} else {
		echo "Invalid input\n\n";
		return askSetFlag($currentFlagSet, $type);
	}
}

function purge($con, $endpointId, $method, $type) {
	$query="DELETE FROM " . $type['pluralCamelCase'] .  " WHERE EndpointId = ".$endpointId." AND Method='".mysqli_real_escape_string($con, $method)."'";
	$result=mysqli_query($con, $query);
	if (!$result) {
		echo "Failed to purge results. Exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	}
	echo "\nPurged " . $type['plural'] .  "\n";
}

function checkStore($type) {
	
	$fh = fopen($type['file'], 'r');
	$count = 0;
	while ($property = fgets($fh)) {
		echo $property;
		$count++;
		if ($count == 10) {
			break;
		}
	}
	echo "\nA sample of what we will add to the database is shown above (each line is inserted separately). Is this correct? [Y/n]\n\n";
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
		return checkStore($type);
	}
}
function doStore($con, $endpointId, $method, $type) {
	echo "Storing " . $type['plural'] .  "\n";
	$fh = fopen($type['file'], 'r');
	$count = 0;
	$values = [];
	while ($property = fgets($fh)) {
		if (strlen($property) > 0) {
			$values[] = '("'.mysqli_real_escape_string($con, trim($property)).'", '. $endpointId. ',"'.mysqli_real_escape_string($con, $method).'")';
			$count++;
		} 
		if ($count % 1000 == 0) {
			$sqlQuery = "INSERT INTO " . $type['pluralCamelCase'] .  " (Uri, EndpointId, Method) VALUES ".implode(',', $values);
			$values = [];
			$result=mysqli_query($con, $sqlQuery);
			if (!$result) {
				echo "Failed to store " . $type['plural'] .  ". Exiting\n";
				echo mysqli_error ($con)."\n";
				exit;
			} else {
				echo $count." stored\n";
			}
		}
	}
	$sqlQuery = "INSERT INTO " . $type['pluralCamelCase'] .  " (Uri, EndpointId, Method) VALUES ".implode(',', $values);
	$result=mysqli_query($con, $sqlQuery);
	if (!$result) {
		echo "Failed to store " . $type['plural'] .  ". Exiting\n";
		echo mysqli_error ($con)."\n";
		exit;
	} else {
		echo $count." stored\n";
	}
	echo "Stored all " . $type['plural'] .  " (".$count.")\n";
}

function askMethod($type) {
	$optionsArray = [
		1 => "query",
		2 => "queryResults"
	];
	echo "\nFor which method do you want to import these " . $type['plural'] .  "? \n".
		"[1] cached from queries (in practice more reliable than [2])\n".
		"[2] retrieved via querying the dataset\n";
	$handle = fopen ("php://stdin","r");
	$chosenMethod = intval(trim(fgets($handle)));
	if($chosenMethod != 1 && $chosenMethod != 2){
		echo "Wrong input. Choose either 1 or 2\n\n";
		return askMethod($type);
	} else {
		return $optionsArray[$chosenMethod];
	}
}

function askEndpoint($type) {
	echo "\nFor which endpoint do you want to import these " . $type['plural'] .  "? \n";
	$handle = fopen ("php://stdin","r");
	$endpoint = trim(fgets($handle));
	if(strlen($endpoint) == 0){
		echo "No input provided\n\n";
		return askEndpoint($type);
	} else {
		return $endpoint;
	}
}

function askPurgeOtherResults($type) {
	$optionsArray = [
		"y" => true,
		"n" => false
	];
	echo "\nFor the endpoint and method you specified, do you want to delete (purge) all existing " . $type['plural'] .  " in the database? [Y/n]\n";
	$handle = fopen ("php://stdin","r");
	$purgeString = strtolower(trim(fgets($handle)));
	if(array_key_exists($purgeString, $optionsArray)) {
		return $optionsArray[$purgeString];
	} else if (strlen($purgeString) == 0) {
		return true;
	} else {
		echo "Invalid input\n\n";
		return askPurgeOtherResults($type);
	}
}

function printSummary($options, $yasguiConfig, $method, $endpoint, $purge, $type) {
	echo "\nBefore proceeding, are the following settings correct?\n";
	echo "We will import the " . $type['plural'] .  " from file ".$type['file'].", for endpoint ".$endpoint." and method ".$method.".\n".
			"All other " . $type['plural'] .  " in our database for this endpoint and method will ".($purge?"":"not ")."be deleted.\n".
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
		return printSummary($options, $method, $endpoint, $purge, $type);
	}
}



