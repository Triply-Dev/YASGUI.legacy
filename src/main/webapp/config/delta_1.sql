CREATE TABLE IF NOT EXISTS `DisabledPropertyEndpoints` (
  `Endpoint` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `LogPropertyFetcher` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Endpoint` varchar(1000) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` enum('successful','failed','fetching') NOT NULL,
  `Pagination` tinyint(1) NOT NULL DEFAULT '0',
  `Message` text,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

CREATE TABLE IF NOT EXISTS `Properties` (
  `Uri` varchar(1000) NOT NULL,
  `Endpoint` varchar(1000) NOT NULL,
  `Method` enum('lazy','property') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
