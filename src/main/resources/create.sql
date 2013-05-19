-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 19, 2013 at 12:20 PM
-- Server version: 5.5.31-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `YASGUI`
--
CREATE DATABASE `YASGUI` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `YASGUI`;

-- --------------------------------------------------------

--
-- Table structure for table `Deltas`
--

CREATE TABLE IF NOT EXISTS `Deltas` (
  `Id` int(11) NOT NULL,
  `Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Queries`
--

CREATE TABLE IF NOT EXISTS `Queries` (
  `UserId` int(11) NOT NULL,
  `Endpoint` varchar(400) CHARACTER SET utf8 NOT NULL,
  `Query` text CHARACTER SET utf8 NOT NULL,
  KEY `UserId` (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Queries`
--

INSERT INTO `Queries` (`UserId`, `Endpoint`, `Query`) VALUES
(1, 'endpoint', 'queryyy');

-- --------------------------------------------------------

--
-- Table structure for table `Session`
--

CREATE TABLE IF NOT EXISTS `Session` (
  `UserId` int(11) NOT NULL,
  `SessionId` varchar(500) CHARACTER SET utf8 NOT NULL,
  `Expire` date NOT NULL,
  UNIQUE KEY `UserId` (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE IF NOT EXISTS `Users` (
  `Id` int(11) NOT NULL,
  `OpenId` varchar(500) NOT NULL,
  `FirstName` varchar(200) CHARACTER SET utf8 NOT NULL,
  `LastName` varchar(200) CHARACTER SET utf8 NOT NULL,
  `Email` varchar(200) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;