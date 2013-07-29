-- phpMyAdmin SQL Dump
-- version 2.11.6
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 29, 2013 at 12:54 PM
-- Server version: 5.0.51
-- PHP Version: 5.2.6

--
-- Database: `android`
--

-- --------------------------------------------------------

--
-- Table structure for table `eventowner`
--

DROP TABLE IF EXISTS `eventowner`;
CREATE TABLE "eventowner" (
  "id" int(11) NOT NULL auto_increment,
  "email" varchar(20) NOT NULL,
  "password" varchar(100) NOT NULL,
  "company_name" varchar(100) default NULL,
  "company_location" varchar(200) default NULL,
  "description_of_services" varchar(300) default NULL,
  PRIMARY KEY  ("id")
) AUTO_INCREMENT=2 ;

--
-- Dumping data for table `eventowner`
--

INSERT INTO `eventowner` (`id`, `email`, `password`, `company_name`, `company_location`, `description_of_services`) VALUES
(1, 'nsubugak@yahoo.com', 'allison', 'brudan', 'hive colab', 'programming');

-- --------------------------------------------------------

--
-- Table structure for table `events_test`
--

DROP TABLE IF EXISTS `events_test`;
CREATE TABLE "events_test" (
  "latitude" varchar(50) default NULL,
  "longitude" varchar(50) default NULL,
  "start_time" varchar(50) default NULL,
  "date" varchar(50) default NULL,
  "description" varchar(100) default NULL,
  "id" int(11) NOT NULL auto_increment,
  "name" varchar(100) default NULL,
  "duration" varchar(100) default NULL,
  "location" varchar(200) default NULL,
  "owner_id" int(11) NOT NULL,
  "type_of_event" varchar(100) NOT NULL,
  PRIMARY KEY  ("id")
) AUTO_INCREMENT=54 ;

--
-- Dumping data for table `events_test`
--

INSERT INTO `events_test` (`latitude`, `longitude`, `start_time`, `date`, `description`, `id`, `name`, `duration`, `location`, `owner_id`, `type_of_event`) VALUES
('0.3388656', '32.5679988', '10:58', '9/7/2013', 'party till late', 27, 'joshs hare', '1 hour', 'here', 0, 'religious'),
('0.3418993', '32.5912342', '11:52', '9/7/2013', 'dddd', 28, 'gamecon', '1 hour', 'here', 0, 'cinema'),
('0.3418993', '32.5912342', '13:23', '9/7/2013', 'buyin an salin', 29, 'marketing ', 'till lunch', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'strike'),
('0.3212435', '32.6010921', '15:2', '14/7/2013', 'clubbing', 35, 'boys night', '1 hour', 'Club Silk, First Street, Kampala, Central Region, Uganda', 1, 'cinema'),
('0.3136111', '32.5811111', '15:3', '17/7/2013', 'we are never eva getting back together', 37, 'never getting ', '4 hour', 'Club Rouge, Kampala, Central Region, Uganda', 0, 'meeting'),
('0.3418993', '32.5912342', '13:23', '17/7/2013', 'coding', 38, 'mobile monday', '1 hour', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'programming'),
('0.3418993', '32.5912342', '11:45', '19/7/2013', 'programming in drupal', 39, 'drupal ninjas', '4 hour', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'programming'),
('0.3212435', '32.6010921', '13:55', '21/7/2013', 'ggg', 44, 'real madrid training', '1 hour', 'First Street, Kampala, Central Region, Uganda', 0, 'meeting'),
('0.6599999999999999', '30.275', '13:55', '21/7/2013', 'ggg', 45, 'finally done', '1 hour', 'Fort Portal, Western Region, Uganda', 0, 'meeting'),
('0.3418993', '32.5912342', '13:23', '9/7/2013', 'buyin an salin', 47, 'marketing ', 'till lunch', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 0, 'strike'),
('0.3418993', '32.5912342', '13:23', '9/7/2013', 'buyin an salin', 48, 'marketing ', 'till lunch', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 0, 'strike'),
('0.3136111', '32.5811111', '15:3', '17/7/2013', 'we are never eva getting back together', 49, 'never getting back togeher', '4 hour', 'Club Rouge, Kampala, Central Region, Uganda', 0, 'meeting'),
('0.3418993', '32.5912342', '10:0', '24/7/2013', 'bbbbb', 51, 'kkk', '1 hour', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'meeting'),
('0.3418993', '32.5912342', '10:43', '24/7/2013', 'hhhh', 52, 'ggg', '1 hour', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'meeting'),
('0.3418993', '32.5912342', '23:4', '28/7/2013', 'tryinggggghhhhhhhhhhhhhhhhhhhhhhhhhhhjjnjjjkkkkkkkkkkkkkkkkkkkllllkjjjjjjjjjj', 53, 'validation', 'till lunch', 'Hive Colab, Kanjokya Road, Kampala, Central Region, Uganda', 1, 'programming');
