<?php
include 'Helper.php';
error_reporting(1);
// $deploymentConfig = parse_init_file("hookConfig.ini", true);

$i = 0;


$payload = $_POST['payload'];

$payload = file_get_contents("payload.json");

if (strlen($payload)) {
	$json = json_decode($payload, true);
	if (!$json) Helper::mailError(__FILE__, __LINE__, "Unable to decode json");
	
	if ($json['ref'] === "refs/heads/master") {
		shell_exec("CompileAndDeploy.php");
	} else if ($json['ref'] === "refs/heads/dev") {
		
	}
} 







