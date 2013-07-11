#!/usr/bin/php
<?php
include_once __DIR__.'/Helper.php';
$config = Helper::getConfig();
if (count($argv) > 1) {
	if (in_array($argv[1], explode(",", $config['deployments']))) {
		$deployConfig = $config[$argv[1]];
		if (is_array($deployConfig) && count($deployConfig)) {
			compileAndDeploy($deployConfig);
		} else {
			Helper::mailError(__FILE__, __LINE__, "couldnt find configuration for ".$argv[1]." in hookConfig.ini");
			exit;
		}
	} else {
		Helper::mailError(__FILE__, __LINE__, "Invalid script argument");
		exit;
	}
} else {
	Helper::mailError(__FILE__, __LINE__, "Not enough arguments");
	exit;
}



function compileAndDeploy($deployConfig) {
	global $argv;
	chdir($deployConfig['git']);
 	pull();
 	package();
	$warFile = getWarFile();
	$yasguiDir = unzipWarFile($warFile);
	updateConfig($yasguiDir, $deployConfig);
	deployToTomcat($yasguiDir, $deployConfig);
 	Helper::sendMail("Succesfully deployed YASGUI as ".$argv[1], "Succesfully deployed YASGUI as ".$argv[1]);
}

function pull() {
	shell_exec("rm errorOutput.txt");
	$result = shell_exec("git pull 2> errorOutput.txt");
	if (file_exists("errorOutput.txt") && strlen(file_get_contents("errorOutput.txt") > 0)) {
		Helper::mailError(__FILE__, __LINE__, "Unable to pull from git: \n".file_get_contents("errorOutput.txt"));
		exit;
	}
}

function package() {
	global $argv;
	$succes = shell_exec("mvn clean 2> errorOutput.txt");
	if (!$succes) {
		Helper::mailError(__FILE__, __LINE__, "Unable to compile ".$argv[1]." project: \n".file_get_contents("errorOutput.txt"));
		exit;
	}
	if ($succes) $succes = shell_exec("mvn package 2> errorOutput.txt");
	if (!$succes || strpos($succes, "BUILD FAILURE")) {
		Helper::mailError(__FILE__, __LINE__, "Unable to compile ".$argv[1]." project: \n".file_get_contents("errorOutput.txt")."\n".$succes);
		exit;
	}
}

function getWarFile() {
	$warFiles = glob("target/*.war");
	if (count($warFiles) != 1) {
		Helper::mailError(__FILE__, __LINE__, "Invalid number of war files after compiling (dir: ".getcwd()."/target/*.war, count: ".count($warFiles).")");
		exit;
	}
	return (reset($warFiles));
}
function unzipWarFile($warFile) {
	global $argv;
	$destination = "/tmp/".$argv[1].time()."_".rand();
	if (file_exists($destination)) {
		Helper::mailError(__FILE__, __LINE__, "Target dir to unzip war in already exists. Something is wrong... (".$destination.")");
		exit;
	}
	$result = shell_exec("unzip ".$warFile." -d ".$destination);
	if ($result == null || !file_exists($destination) || count(scandir($destination)) <= 2) {
		Helper::mailError(__FILE__, __LINE__, "Failed to unzip compiled war file ".$warFile);
		exit;
	}
	return $destination;
}
function updateConfig($dir, $deployConfig) {
	$newConfig = getUpdatedConfig($dir, $deployConfig);
	if (is_array($newConfig) && count($newConfig)) {
		file_put_contents($dir."/config/config.json", json_encode($newConfig,JSON_UNESCAPED_SLASHES));
		if (!file_exists($dir."/config/config.json")) {
			Helper::mailError(__FILE__, __LINE__, "unable to store new json config file");
			exit;
		}
	} else {
		Helper::mailError(__FILE__, __LINE__, "unable to get updated config array");
		exit;
	}
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
	if (!(is_array($jsonConfigArray) && count($jsonConfigArray))) {
		Helper::mailError(__FILE__, __LINE__, "Unable to parse file as json (".$jsonConfig.")");
		exit;
	}
	$overWriteJsonConfigArray = json_decode(file_get_Contents($overWriteJsonConfig), true);
	if (!(is_array($overWriteJsonConfigArray) && count($jsonConfigArray))) {
		Helper::mailError(__FILE__, __LINE__, "Unable to parse file as json (".$overWriteJsonConfig.")");
		exit;
	}
	return array_replace_recursive($jsonConfigArray, $overWriteJsonConfigArray);
}
function deployToTomcat($yasguiDir, $deployConfig) {
	global $config;
	$to = $deployConfig['tomcat'];
	
	/**
	 * Remove current deployment
	 */
	//be very sure we arent deleting other stuff:
	if (strlen($to) && file_exists($to) && strpos($to, "tomcat")) {
		//first copy cache dir (we want to save that one)
		shell_exec("mv ".$to."/config ".sys_get_temp_dir());
		
		shell_exec("rm -rf ".$to);
		//all files created by YASGUI won't be deleted... (owned by tomcat, not apache)
		if (file_exists($to)) {
			Helper::mailError(__FILE__, __LINE__, "Unable to remove previously deployed yasgui dir: ".$to.". It still exists!");
			exit;
		}
	}
	if (!file_exists($yasguiDir)) {
		Helper::mailError(__FILE__, __LINE__, "We have no directory to copy. Something is wrong!");
		exit;
	}
	
	/**
	Deploy new dir
	 */
	$result = shell_exec("mv ".$yasguiDir." ".$to);
	if (!file_exists($to)) {
		Helper::mailError(__FILE__, __LINE__, "Failed to copy yasgui to tomcat dir");
		exit;
	}
	
	
	/**
	 * Set proper permissions
	 */
	shell_exec("chmod -R 775 ".$to);
	
	/**
	 * restore cach dir
	 */
	shell_exec("mv ".sys_get_temp_dir()."/config/* ".$to."/config ");
	
}

