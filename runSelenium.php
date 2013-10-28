#!/usr/bin/php
<?php

$debug = false;
$cleanedArgs = array();
foreach ($argv as $arg) {
        if ($arg == "debug") {
                $debug = true;
        } else {
                $cleanedArgs[] = $arg;
        }
}
error_reporting(E_ERROR);
include_once __DIR__.'/Helper.php';
$config = Helper::getConfig();
$seleniumRuns = array();
if (count($cleanedArgs) > 1) {
        if (is_array($config['deployTargets'][$cleanedArgs[1]])) {
                foreach ($config['deployTargets'][$cleanedArgs[1]] as $deployConfig) {
                	if (array_key_exists("checkSeleniumHost",$deployConfig)) $seleniumRuns[] = $deployConfig;
                }
        }
} else {
        echo "not enough arguments: \n ./runSelenium.php <branch>\n";
}
$seleniumResults = runSelenium($seleniumRuns);
function runSelenium($deployConfigs) {
	global $config;
	foreach ($deployConfigs as $deployConfig) {
		echo "Running selenium tests for ".$deployConfig['checkSeleniumHost']."\n";
		//create java props array
		$propsArray = array();
		$propsArray[] = "sendMail=true";
		$propsArray[] = "mailUserName=".$config['mailSettings']['username'];
		$propsArray[] = "mailPassword=".$config['mailSettings']['password'];
		$propsArray[] = "mailSendTo=".$config['mailSettings']['to'];
		$propsArray[] = "checkHost=".$deployConfig['checkSeleniumHost'];
		
		//write props to file
		chdir($deployConfig['src']);
		file_put_contents("bin/selenium/selenium.properties", implode("\n", $propsArray));
		
		$result = shell_exec("export DISPLAY=:99; mvn test -DskipTests=false 2> errorOutput.txt");
		if (strpos($result, "There are test failures") !== false) {
			return "Unable to run selenium tests: \n".str_replace("\n", "<br>", $result);
		}
	}
}

