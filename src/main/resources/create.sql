-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 23, 2013 at 10:29 AM
-- Server version: 5.5.31-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `YASGUI`
--
CREATE DATABASE `YASGUI` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `YASGUI`;

-- --------------------------------------------------------

--
-- Table structure for table `Bookmarks`
--

CREATE TABLE IF NOT EXISTS `Bookmarks` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL,
  `Endpoint` text CHARACTER SET utf8,
  `Query` text CHARACTER SET utf8 NOT NULL,
  `Title` text,
  PRIMARY KEY (`Id`),
  KEY `UserId` (`UserId`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=18 ;

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
-- Table structure for table `Users`
--

CREATE TABLE IF NOT EXISTS `Users` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `OpenId` text CHARACTER SET utf8 NOT NULL,
  `UniqueId` varchar(12) CHARACTER SET utf8 NOT NULL,
  `FirstName` text CHARACTER SET utf8,
  `LastName` text CHARACTER SET utf8,
  `FullName` text,
  `NickName` text,
  `Email` text,
  `LastLogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;
