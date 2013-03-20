<?php

class Helper {
	 
	static function sendMail($subject, $body) {
		$config = Helper::getConfig();
		if ($config) {
			require_once(__DIR__.'/lib/PHPMailer_v5.1/class.phpmailer.php');
			$mail = new PHPMailer();  // create a new object
			$mail->IsSMTP(); // enable SMTP
			$mail->SMTPDebug = 0;  // debugging: 1 = errors and messages, 2 = messages only
			$mail->SMTPAuth = true;  // authentication enabled
			$mail->SMTPSecure = 'ssl'; // secure transfer enabled REQUIRED for GMail
			$mail->Host = 'smtp.gmail.com';
			$mail->Port = 465;
			$mail->Username = $config['mail']['username'];
			$mail->Password = $config['mail']['password'];
			$mail->SetFrom($config['mail']['from'], ['YASGUI commit hook']);
			$mail->Subject = $subject;
			$mail->MsgHTML($body);
			$mail->AddAddress($config['mail']['to']);
			return $mail->Send();
		} else {
			return false;
		}
	}
	
	static function getConfig() {
		$config = parse_ini_file(__DIR__."/hookConfig.ini", true);
		if ($config == null || !is_array($config) || count($config) == 0) {
			Helper::mailError(__FILE__, __LINE__, "Unable to load hook config ini file");
			exit;
		}
		return $config;
	}
	
	static function mailError($file, $line, $error) {
 		$body = "<html><body>";
 		$body .= "File: ".$file." (line ". $line .")<br>";
 		$body .= "Time: ".date("Y-m-d H:i:s")."<br>";
 		$body .= "Error: <strong>".$error."</strong><br>";
	
 		$body .= "</body></html>";
 		Helper::sendMail("Error in ".basename(__FILE__), $body);
//		echo $error."\n";
//		file_put_contents(__DIR__."/error.txt", $error."\n", FILE_APPEND);
	}
	
	static function execWithError($cmd, $file, $line, $errorMsg) {
		$result = shell_exec($cmd."  2> errorOutput.txt");
		if ($result == null) {
			Helper::mailError($file, $line, $errorMsg."\n".file_get_contents("errorOutput.txt"));
			exit;
		}
	}
}