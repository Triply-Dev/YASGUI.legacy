<?php
error_reporting(1);
// $deploymentConfig = parse_init_file("hookConfig.ini", true);
$payload = $_POST['payload'];

$payload = file_get_contents("payload.json");

if (strlen($payload)) {
	$json = json_decode($payload, true);
	if (!$json) mailError("Unable to decode json");
	
	var_export($json);
} 



function mailError($error) {
	echo $error."\n";exit;
}

//add mail function faulure notification and stuff

//parse json

//if commit on dev
////pull result
////build
////parse/replace/store config values
////copy to tomcat

//if commit on master


