#!/usr/bin/php
<?php
include_once __DIR__.'/Helper.php';
$config = Helper::getConfig();
if (count($argv) > 1) {
	if ($argv[1] === "dev" || $argv[1] === "master") {
		compileAndDeploy($config[$argv[1]]);
	} else {
		Helper::mailError(__FILE__, __LINE__, "Invalid script argument");
	}
} else {
	Helper::mailError(__FILE__, __LINE__, "Not enough arguments");
}



function compileAndDeploy($deployConfig) {
	chdir($deployConfig['git']);
	pull();
	package();
	$warFile = getWarFile();
	$yasguiDir = unzipWarFile($warFile);
	updateConfig($yasguiDir, $deployConfig);
	deployToTomcat($yasguiDir, $deployConfig);
	Helper::sendMail("Deployed to ".$argv[1], "Succesfully deployed YASGUI");
}

function pull() {
	if (shell_exec("git pull") === null) {
		Helper::mailError(__FILE__, __LINE__, "Unable to pull ".$argv[1]." from git");
		exit;
	}
}

function package() {
	$succes = shell_exec("mvn clean");
	if ($succes) $succes = shell_exec("mvn package");
	if (!$succes) {
		Helper::mailError(__FILE__, __LINE__, "Unable to compile ".$argv[1]." project");
		exit;
	}
}

function getWarFile() {
	$warFiles = glob("target/*.war");
	if (count($warFiles) != 1) {
		Helper::mailError(__FILE__, __LINE__, "Invalid number of war files after compilating (".count($warFiles).")");
		exit;
	}
	return (reset($warFiles));
}
function unzipWarFile($warFile) {
	$destination = "target/unzipped";
	if (file_exists($destination)) {
		Helper::mailError(__FILE__, __LINE__, "Target dir to unzip war in already exists. Something is wrong... (".$destination.")");
		exit;
	}
	$result = shell_exec("unzip ".$warFile." -d ".$destination);
	if ($result == null || count(scandir($destination)) <= 2) {
		Helper::mailError(__FILE__, __LINE__, "Failed to unzip compiled war file ".$warFile);
		exit;
	}
	return $destination;
}
function updateConfig($dir, $deployConfig) {
	$newConfig = getUpdatedConfig($dir, $deployConfig);
	file_put_contents($dir."/config/config.json", json_encode($newConfig,JSON_UNESCAPED_SLASHES));
}

function getUpdatedConfig($dir, $deployConfig) {
	$jsonConfig = $dir."/config/config.json";
	if (!file_exists($jsonConfig)) {
		Helper::mailError(__FILE__, __LINE__, "No config file in unzipped war file (".$jsonConfig.")");
		exit;
	}
	$overWriteJsonConfig = $deployConfig['yasguiConfig'];
	if (!file_exists($overWriteJsonConfig)) {
		Helper::mailError(__FILE__, __LINE__, "No json config file to apply to yasgui (".$overWriteJsonConfig.")");
		exit;
	}
	
	$jsonConfigArray = json_decode(file_get_contents($jsonConfig), true);
	if ($jsonConfigArray == null) {
		Helper::mailError(__FILE__, __LINE__, "Unable to parse file as json (".$jsonConfig.")");
		exit;
	}
	$overWriteJsonConfigArray = json_decode(file_get_Contents($overWriteJsonConfig), true);
	if ($jsonConfigArray == null) {
		Helper::mailError(__FILE__, __LINE__, "Unable to parse file as json (".$overWriteJsonConfig.")");
		exit;
	}
	return array_replace_recursive($jsonConfigArray, $overWriteJsonConfigArray);
}
function deployToTomcat($yasguiDir, $deployConfig) {
	$to = $deployConfig['tomcat'];
	if (strlen($to) && file_exists($to) && strpos($to, "tomcat")) {
		//be very sure we arent deleting other stuff
		shell_exec("rm -rf ".$to);
	}
	$result = shell_exec("cp -r ".$yasguiDir." ".$to);
	if (!$result) {
		Helper::mailError(__FILE__, __LINE__, "Failed to copy yasgui to tomcat dir");
		exit;
	}
	
}

