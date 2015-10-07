-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Oct 07, 2015 at 06:12 PM
-- Server version: 5.6.26
-- PHP Version: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `wiktionary`
--

-- --------------------------------------------------------

--
-- Table structure for table `word_entry`
--

CREATE TABLE IF NOT EXISTS `word_entry` (
  `word` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `language` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `pronunciation` varchar(3000) COLLATE utf8_unicode_ci NOT NULL,
  `etymology` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `pos` varchar(3000) COLLATE utf8_unicode_ci NOT NULL,
  `pos_detail` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `src_core` varchar(1001) COLLATE utf8_unicode_ci NOT NULL,
  `src_detail` varchar(1001) COLLATE utf8_unicode_ci NOT NULL,
  `pos_c4` varchar(1001) COLLATE utf8_unicode_ci NOT NULL,
  `pos_c5` varchar(1001) COLLATE utf8_unicode_ci NOT NULL,
  `definition` varchar(512) COLLATE utf8_unicode_ci NOT NULL,
  `example` varchar(512) COLLATE utf8_unicode_ci NOT NULL,
  `synonyms` text COLLATE utf8_unicode_ci NOT NULL,
  `antonyms` text COLLATE utf8_unicode_ci NOT NULL,
  `related` text COLLATE utf8_unicode_ci NOT NULL,
  `count` varchar(512) COLLATE utf8_unicode_ci NOT NULL,
  `translation` text COLLATE utf8_unicode_ci NOT NULL,
  `references` text COLLATE utf8_unicode_ci NOT NULL,
  `extra` text COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `word_entry`
--
ALTER TABLE `word_entry`
  ADD KEY `word` (`word`(255));

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
