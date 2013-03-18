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
		return parse_ini_file(__DIR__."/hookConfig.ini", true);
	}
	
	static function mailError($file, $line, $error) {
		$body = "<html><body>";
		$body .= "File: ".$file." (line ". $line .")<br>";
		$body .= "Time: ".date("Y-m-d H:i:s")."<br>";
		$body .= "Error: <strong>".$error."</strong><br>";
	
		$body .= "</body></html>";
		Helper::sendMail("Error in ".basename(__FILE__), $body);
	}
}