CREATE TABLE IF NOT EXISTS `Classes` (
  `Uri` varchar(1000) NOT NULL,
  `Endpoint` varchar(1000) NOT NULL,
  `Method` enum('query','queryResults') NOT NULL,
  `Added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `DisabledClassEndpoints` (
  `Endpoint` varchar(1000) NOT NULL,
  `Method` enum('query','queryResults') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `LogClassFetcher` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Endpoint` varchar(1000) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` enum('successful','failed','fetching') NOT NULL,
  `Pagination` tinyint(1) NOT NULL DEFAULT '0',
  `Message` text,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;