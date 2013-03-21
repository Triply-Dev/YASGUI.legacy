<?php

$startTime = time();
chdir("/home/lrd900/gitCode/yasgui_deploy");
include 'Helper.php';
error_reporting(1);
$config = Helper::getConfig();

$payload = $_POST['payload'];



if (strlen($payload)) {
	$json = json_decode($payload, true);
	if (!$json) Helper::mailError(__FILE__, __LINE__, "Unable to decode json");
	
	if ($json['ref'] === "refs/heads/master") {
		executeIfReady("./CompileAndDeploy.php master");
	} else if ($json['ref'] === "refs/heads/dev") {
		executeIfReady("./CompileAndDeploy.php dev");
	} else if ($json['ref'] === "refs/heads/deployment-git-hook") {
		Helper::execWithError("git stash", __FILE__, __LINE__, "Unable to stash changes of git hook code");
		Helper::execWithError("git pull", __FILE__, __LINE__, "Unable to pull git hub code");
		Helper::execWithError("git stash pop", __FILE__, __LINE__, "Unable to apply stashed changes of git code");
		Helper::sendMail("Succesfully updated commit hook code", "Succesfully updated commit hook code");
	}
} 

function executeIfReady($command) {
	global $startTime, $config;
	
	if ((time() - $startTime) > ($config['shell']['maxTimeout'] * 60)) {
		Helper::mailError(__FILE__, __LINE__, "We waited too long for other processes to finish. Just stop trying to deploy. Number of processes still running: ".getNumProcesses($command));
		exit;
	}
	$numProcs = getNumProcesses($command);
	if ($numProcs > 0) {
		sleep(10);
		executeIfReady($command);
	} else {
		shell_exec($command ." > /dev/null 2>&1 &");
	}
}
function getNumProcesses($cmd) {
	return shell_exec("ps aux | grep '/[u]sr/bin/php ".$cmd."' | wc -l");
}







