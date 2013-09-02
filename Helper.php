<?php

class Helper {
	 
	static function sendMail($subject, $body) {
		$config = Helper::getConfig();
		if (is_array($config)) {
			require_once(__DIR__.'/lib/PHPMailer_v5.1/class.phpmailer.php');
			$mail = new PHPMailer();  // create a new object
			$mail->IsSMTP(); // enable SMTP
			$mail->SMTPDebug = 0;  // debugging: 1 = errors and messages, 2 = messages only
			$mail->SMTPAuth = true;  // authentication enabled
			$mail->SMTPSecure = 'ssl'; // secure transfer enabled REQUIRED for GMail
			$mail->Host = 'smtp.gmail.com';
			$mail->Port = 465;
			$mail->Username = $config['mailSettings']['username'];
			$mail->Password = $config['mailSettings']['password'];
			$mail->SetFrom($config['mailSettings']['from'], 'YASGUI commit hook');
			$mail->Subject = $subject;
			$mail->MsgHTML($body);
			$mail->AddAddress($config['mailSettings']['to']);
			return $mail->Send();
		} else {
			return false;
		}
	}
	
	static function getConfig() {
		$config = json_decode(file_get_contents(__DIR__."/config.json"), true);
		if ($config == null || !is_array($config) || count($config) == 0) {
			Helper::mailError(__FILE__, __LINE__, "Unable to load main config json file ".$config);
		}
		return $config;
	}
	
	static function mailError($file, $line, $error) {
		global $debug;
		if ($debug) {
			echo "File: ".$file." (#".$line.")\n";
			echo "Error: ".$error."\n";
		} else {
			$body = "<html><body>";
			$body .= "File: ".$file." (line ". $line .")<br>";
			$body .= "Time: ".date("Y-m-d H:i:s")."<br>";
			$body .= "Error: <strong>".$error."</strong><br>";
			
			$body .= "</body></html>";
			Helper::sendMail("Error in ".basename(__FILE__)." on ".gethostname(), $body);
		}
 		exit;
	}
	
	static function execWithError($cmd, $file, $line, $errorMsg) {
		$result = shell_exec($cmd."  2> errorOutput.txt");
		if ($result == null) {
			Helper::mailError($file, $line, $errorMsg."\n".file_get_contents("errorOutput.txt"));
			exit;
		}
	}
}
