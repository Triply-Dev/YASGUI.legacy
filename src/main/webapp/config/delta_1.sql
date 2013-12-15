
--
-- Table structure for table `AccessibleEndpoints`
--

CREATE TABLE IF NOT EXISTS `AccessibleEndpoints` (
  `EndpointId` int(11) NOT NULL,
  `Accessible` tinyint(1) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Classes`
--

CREATE TABLE IF NOT EXISTS `Classes` (
  `Uri` varchar(1000) NOT NULL,
  `EndpointId` int(11) NOT NULL,
  `Method` enum('query','queryResults') NOT NULL,
  `Added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `CompletionEndpoints`
--

CREATE TABLE IF NOT EXISTS `CompletionEndpoints` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Endpoint` varchar(1000) NOT NULL,
  `UserId` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Table structure for table `CompletionsLog`
--

CREATE TABLE IF NOT EXISTS `CompletionsLog` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `EndpointId` int(11) NOT NULL,
  `Type` enum('class','property') NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` enum('successful','failed','fetching') NOT NULL,
  `Pagination` tinyint(1) NOT NULL DEFAULT '0',
  `Message` text,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=34 ;

-- --------------------------------------------------------

--
-- Table structure for table `DisabledCompletionEndpoints`
--

CREATE TABLE IF NOT EXISTS `DisabledCompletionEndpoints` (
  `EndpointId` int(11) NOT NULL,
  `Type` enum('class','property') NOT NULL,
  `Method` enum('query','queryResults') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Properties`
--

CREATE TABLE IF NOT EXISTS `Properties` (
  `Uri` varchar(1000) NOT NULL,
  `EndpointId` int(11) NOT NULL,
  `Method` enum('query','queryResults') NOT NULL,
  `Added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
