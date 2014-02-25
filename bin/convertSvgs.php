#!/usr/bin/php
<?php




	$imgConfigs = array(
		"playSquare" => array(
			"size" => 48
		),
		"playLow" => array(
			"ar" => "100:71",
			"size" => 48
		),
		"arrows" => array(
			"size" => 19
		),
		"arrowDown" => array(
				"size" => 19
		),
		"arrowUp" => array(
				"size" => 19
		),
// 		"crossThin" => array(
// 			"size" => 11
// 		),
		"crossSmall" => array(
			"size" => 12
		),
		"plus" => array(
			"size" => 12
		)
	);
	$imgs = array();
	if (count($argv) > 1) {
		array_shift($argv);
		$imgs = $argv;
	} else {
		$imgs = findSvgs("public");
	}
	echo "processing ". count($imgs)." svgs\n";
	foreach($imgs AS $key => $img) {
		
		$percent = round(($key + 1) / count($imgs) * 100);
		echo "\r".$percent."% completed";
// 		echo $img."\n";
		convert($img);
		
// 		echo "\n";
// 		exit;
	}
	shell_exec("rm -f tmp.svg");
	echo "\n";
	function findSvgs($dir) {
		$imgs = array();
		if ($handle = opendir($dir)) {
			while (false !== ($entry = readdir($handle))) {
				if ($entry[0] != ".") {
					$entry = $dir."/".$entry;
					if (is_dir($entry)) {
						$imgs = array_merge($imgs, findSvgs($entry));
					} else {
						if (strpos($entry, "font") === false && pathinfo($entry, PATHINFO_EXTENSION) == "svg") {
							$imgs[] = $entry;
						}
					}
				}
				
			}
			closedir($handle);
		}
		return $imgs;
	}
	
	function getPngFilename($img, $append = null) {
		$pathInfo = pathinfo($img);
		return $pathInfo['dirname']."/".$pathInfo['filename'].($append != null? "_".$append: "").".png";
	}
	function getWhiteFile($img) {
		return getFileInColor($img, "#ffffff");
	}
	function getDisabledFile($img) {
		return getFileInColor($img, "#A5A5A5");
	}
	
	function removeColorSettings($svgString) {
		$pattern = '/(fill|stroke)=["\'](\w|#)+["\']/i';
		$replacement = '';
		$result =  preg_replace($pattern, $replacement, $svgString);
// 		echo $result;
// 		exit;
		return $result;
		
		
	}
	function getFileInColor($img, $color) {
		$svgContents = file_get_contents($img);
		$svgContents = removeColorSettings($svgContents);
		
		$svgContents = str_replace("<path", "<path fill='".$color."' stroke='".$color."'", $svgContents);
		$svgContents = str_replace("<polygon", "<polygon fill='".$color."' stroke='".$color."'", $svgContents);
		file_put_contents("tmp.svg", $svgContents);
		return "tmp.svg";
	}
	
	function convert($img) {
		global $imgConfigs;
// 		$pathInfo = pathinfo($img);
// 		$png = $pathInfo['dirname']."/".$pathInfo['filename'].".png";
// 		echo $png;
		$pathInfo = pathinfo($img);
		$cmd = "inkscape -f ".$img." -e ".getPngFilename($img);
		
		$width = 24;
		$height = 24;
		//$size = 24;
		
		if (array_key_exists($pathInfo['filename'], $imgConfigs)) {
			$imgConfig = $imgConfigs[$pathInfo['filename']];
			if (array_key_exists("size", $imgConfig)) {
				$width = $imgConfig["size"];
				$height = $imgConfig["size"];
			}
			if (array_key_exists("ar", $imgConfig)) {
				$ar = $imgConfig["ar"];
				list($widthAr, $heightAr) = explode(":", $ar);
				if ($width <= $height) {
					//only change height
					$height = ($height / ($widthAr / $heightAr));
				} else {
					$width = ($width / ($widthAr / $heightAr));
				}
			}
		}
		shell_exec(getInkscapeCmd($img, getPngFilename($img), $width, $height));
		
		$tmpFile = getDisabledFile($img);
		shell_exec(getInkscapeCmd($tmpFile, getPngFilename($img, "disabled"), $width, $height));
		
		$tmpFile = getWhiteFile($img);
		shell_exec(getInkscapeCmd($tmpFile, getPngFilename($img, "white"), $width, $height));
	}
	function getInkscapeCmd($orig, $target, $width, $height) {
		$cmd = "inkscape -f ".$orig." -e ".$target." --export-width=". $width ." --export-height=".$height;
		return $cmd;
	}
