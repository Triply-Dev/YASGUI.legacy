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
	
}

