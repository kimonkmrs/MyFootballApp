-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: footballapp
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matches` (
  `MatchID` int NOT NULL AUTO_INCREMENT,
  `Team1ID` int DEFAULT NULL,
  `Team2ID` int DEFAULT NULL,
  `ScoreTeam1` int DEFAULT NULL,
  `ScoreTeam2` int DEFAULT NULL,
  `MatchDate` datetime DEFAULT NULL,
  `Team1Name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `Team2Name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`MatchID`),
  KEY `Team1ID` (`Team1ID`),
  KEY `Team2ID` (`Team2ID`),
  CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`Team1ID`) REFERENCES `teams` (`TeamID`),
  CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`Team2ID`) REFERENCES `teams` (`TeamID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matches`
--

LOCK TABLES `matches` WRITE;
/*!40000 ALTER TABLE `matches` DISABLE KEYS */;
INSERT INTO `matches` VALUES (1,1,2,2,1,'2024-09-01 13:00:00','Team A1','Team A2'),(2,3,4,0,3,'2024-09-02 12:00:00','Team A3','Team A4'),(3,5,6,1,1,'2024-09-03 15:00:00','Team A5','Team A6'),(4,1,10,3,0,'2024-09-04 14:00:00','Team A1','Team A10'),(5,9,7,2,2,'2024-09-05 17:00:00','Team A9','Team A7'),(6,3,11,2,2,'2024-09-01 12:00:00','Team A3','Team B1'),(7,13,12,1,4,'2024-09-02 16:00:00','Team B3','Team B2'),(8,6,3,3,1,'2024-09-06 13:00:00','Team A6','Team A3'),(10,2,7,0,0,'2024-09-08 14:30:00','Team A2','Team A7'),(12,13,16,2,3,'2024-09-08 11:00:00','Team B3','Team B6');
/*!40000 ALTER TABLE `matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playermatches`
--

DROP TABLE IF EXISTS `playermatches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playermatches` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `PlayerID` int DEFAULT NULL,
  `MatchID` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `PlayerID` (`PlayerID`,`MatchID`),
  KEY `MatchID` (`MatchID`),
  CONSTRAINT `playermatches_ibfk_1` FOREIGN KEY (`PlayerID`) REFERENCES `players` (`PlayerID`) ON DELETE CASCADE,
  CONSTRAINT `playermatches_ibfk_2` FOREIGN KEY (`MatchID`) REFERENCES `matches` (`MatchID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playermatches`
--

LOCK TABLES `playermatches` WRITE;
/*!40000 ALTER TABLE `playermatches` DISABLE KEYS */;
/*!40000 ALTER TABLE `playermatches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `players` (
  `PlayerID` int NOT NULL,
  `TeamID` int DEFAULT NULL,
  `PlayerName` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `Position` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `GoalsScored` int DEFAULT '0',
  `GoalsConceded` int DEFAULT '0',
  `Wins` int DEFAULT '0',
  `Losses` int DEFAULT '0',
  `Draws` int DEFAULT '0',
  `YellowCards` int DEFAULT '0',
  `RedCards` int DEFAULT '0',
  `MatchId` int DEFAULT NULL,
  PRIMARY KEY (`PlayerID`),
  KEY `TeamID` (`TeamID`),
  KEY `fk_match` (`MatchId`),
  CONSTRAINT `fk_match` FOREIGN KEY (`MatchId`) REFERENCES `matches` (`MatchID`),
  CONSTRAINT `players_ibfk_1` FOREIGN KEY (`TeamID`) REFERENCES `teams` (`TeamID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `players`
--

LOCK TABLES `players` WRITE;
/*!40000 ALTER TABLE `players` DISABLE KEYS */;
INSERT INTO `players` VALUES (1,1,'Player Team A1-1','Forward',0,0,0,0,0,0,0,NULL),(2,1,'Player Team A1-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(3,1,'Player Team A1-3','Defender',0,0,0,0,0,0,0,NULL),(4,1,'Player Team A1-4','Midfielder',0,0,0,0,0,0,0,NULL),(5,1,'Player Team A1-5','Forward',0,0,0,0,0,0,0,NULL),(6,2,'Player Team A2-1','Forward',0,0,0,0,0,0,0,NULL),(7,2,'Player Team A2-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(8,2,'Player Team A2-3','Defender',0,0,0,0,0,0,0,NULL),(9,2,'Player Team A2-4','Midfielder',0,0,0,0,0,0,0,NULL),(10,2,'Player Team A2-5','Forward',0,0,0,0,0,0,0,NULL),(11,3,'Player Team A3-1','Forward',0,0,0,0,0,0,0,NULL),(12,3,'Player Team A3-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(13,3,'Player Team A3-3','Defender',0,0,0,0,0,0,0,NULL),(14,3,'Player Team A3-4','Midfielder',0,0,0,0,0,0,0,NULL),(15,3,'Player Team A3-5','Forward',0,0,0,0,0,0,0,NULL),(16,4,'Player Team A4-1','Forward',0,0,0,0,0,0,0,NULL),(17,4,'Player Team A4-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(18,4,'Player Team A4-3','Defender',0,0,0,0,0,0,0,NULL),(19,4,'Player Team A4-4','Midfielder',0,0,0,0,0,0,0,NULL),(20,4,'Player Team A4-5','Forward',0,0,0,0,0,0,0,NULL),(21,5,'Player Team A5-1','Forward',0,0,0,0,0,0,0,NULL),(22,5,'Player Team A5-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(23,5,'Player Team A5-3','Defender',0,0,0,0,0,0,0,NULL),(24,5,'Player Team A5-4','Midfielder',0,0,0,0,0,0,0,NULL),(25,5,'Player Team A5-5','Forward',0,0,0,0,0,0,0,NULL),(26,6,'Player Team A6-1','Forward',0,0,0,0,0,0,0,NULL),(27,6,'Player Team A6-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(28,6,'Player Team A6-3','Defender',0,0,0,0,0,0,0,NULL),(29,6,'Player Team A6-4','Midfielder',0,0,0,0,0,0,0,NULL),(30,6,'Player Team A6-5','Forward',0,0,0,0,0,0,0,NULL),(31,7,'Player Team A7-1','Forward',0,0,0,0,0,0,0,NULL),(32,7,'Player Team A7-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(33,7,'Player Team A7-3','Defender',0,0,0,0,0,0,0,NULL),(34,7,'Player Team A7-4','Midfielder',0,0,0,0,0,0,0,NULL),(35,7,'Player Team A7-5','Forward',0,0,0,0,0,0,0,NULL),(36,8,'Player Team A8-1','Forward',0,0,0,0,0,0,0,NULL),(37,8,'Player Team A8-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(38,8,'Player Team A8-3','Defender',0,0,0,0,0,0,0,NULL),(39,8,'Player Team A8-4','Midfielder',0,0,0,0,0,0,0,NULL),(40,8,'Player Team A8-5','Forward',0,0,0,0,0,0,0,NULL),(41,9,'Player Team A9-1','Forward',0,0,0,0,0,0,0,NULL),(42,9,'Player Team A9-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(43,9,'Player Team A9-3','Defender',0,0,0,0,0,0,0,NULL),(44,9,'Player Team A9-4','Midfielder',0,0,0,0,0,0,0,NULL),(45,9,'Player Team A9-5','Forward',0,0,0,0,0,0,0,NULL),(46,10,'Player Team A10-1','Forward',0,0,0,0,0,0,0,NULL),(47,10,'Player Team A10-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(48,10,'Player Team A10-3','Defender',0,0,0,0,0,0,0,NULL),(49,10,'Player Team A10-4','Midfielder',0,0,0,0,0,0,0,NULL),(50,10,'Player Team A10-5','Forward',0,0,0,0,0,0,0,NULL),(51,11,'Player Team B1-1','Forward',0,0,0,0,0,0,0,NULL),(52,11,'Player Team B1-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(53,11,'Player Team B1-3','Defender',0,0,0,0,0,0,0,NULL),(54,11,'Player Team B1-4','Midfielder',0,0,0,0,0,0,0,NULL),(55,11,'Player Team B1-5','Forward',0,0,0,0,0,0,0,NULL),(56,12,'Player Team B2-1','Forward',0,0,0,0,0,0,0,NULL),(57,12,'Player Team B2-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(58,12,'Player Team B2-3','Defender',0,0,0,0,0,0,0,NULL),(59,12,'Player Team B2-4','Midfielder',0,0,0,0,0,0,0,NULL),(60,12,'Player Team B2-5','Forward',0,0,0,0,0,0,0,NULL),(61,13,'Player Team B3-1','Forward',0,0,0,0,0,0,0,NULL),(62,13,'Player Team B3-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(63,13,'Player Team B3-3','Defender',0,0,0,0,0,0,0,NULL),(64,13,'Player Team B3-4','Midfielder',0,0,0,0,0,0,0,NULL),(65,13,'Player Team B3-5','Forward',0,0,0,0,0,0,0,NULL),(66,14,'Player Team B4-1','Forward',0,0,0,0,0,0,0,NULL),(67,14,'Player Team B4-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(68,14,'Player Team B4-3','Defender',0,0,0,0,0,0,0,NULL),(69,14,'Player Team B4-4','Midfielder',0,0,0,0,0,0,0,NULL),(70,14,'Player Team B4-5','Forward',0,0,0,0,0,0,0,NULL),(71,15,'Player Team B5-1','Forward',0,0,0,0,0,0,0,NULL),(72,15,'Player Team B5-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(73,15,'Player Team B5-3','Defender',0,0,0,0,0,0,0,NULL),(74,15,'Player Team B5-4','Midfielder',0,0,0,0,0,0,0,NULL),(75,15,'Player Team B5-5','Forward',0,0,0,0,0,0,0,NULL),(76,16,'Player Team B6-1','Forward',0,0,0,0,0,0,0,NULL),(77,16,'Player Team B6-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(78,16,'Player Team B6-3','Defender',0,0,0,0,0,0,0,NULL),(79,16,'Player Team B6-4','Midfielder',0,0,0,0,0,0,0,NULL),(80,16,'Player Team B6-5','Forward',0,0,0,0,0,0,0,NULL),(81,17,'Player Team B7-1','Forward',0,0,0,0,0,0,0,NULL),(82,17,'Player Team B7-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(83,17,'Player Team B7-3','Defender',0,0,0,0,0,0,0,NULL),(84,17,'Player Team B7-4','Midfielder',0,0,0,0,0,0,0,NULL),(85,17,'Player Team B7-5','Forward',0,0,0,0,0,0,0,NULL),(86,18,'Player Team B8-1','Forward',0,0,0,0,0,0,0,NULL),(87,18,'Player Team B8-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(88,18,'Player Team B8-3','Defender',0,0,0,0,0,0,0,NULL),(89,18,'Player Team B8-4','Midfielder',0,0,0,0,0,0,0,NULL),(90,18,'Player Team B8-5','Forward',0,0,0,0,0,0,0,NULL),(91,19,'Player Team B9-1','Forward',0,0,0,0,0,0,0,NULL),(92,19,'Player Team B9-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(93,19,'Player Team B9-3','Defender',0,0,0,0,0,0,0,NULL),(94,19,'Player Team B9-4','Midfielder',0,0,0,0,0,0,0,NULL),(95,19,'Player Team B9-5','Forward',0,0,0,0,0,0,0,NULL),(96,20,'Player Team B10-1','Forward',0,0,0,0,0,0,0,NULL),(97,20,'Player Team B10-2','Goalkeeper',0,0,0,0,0,0,0,NULL),(98,20,'Player Team B10-3','Defender',0,0,0,0,0,0,0,NULL),(99,20,'Player Team B10-4','Midfielder',0,0,0,0,0,0,0,NULL),(100,20,'Player Team B10-5','Forward',0,0,0,0,0,0,0,NULL);
/*!40000 ALTER TABLE `players` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teamgroups`
--

DROP TABLE IF EXISTS `teamgroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teamgroups` (
  `GroupID` int NOT NULL,
  `GroupName` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teamgroups`
--

LOCK TABLES `teamgroups` WRITE;
/*!40000 ALTER TABLE `teamgroups` DISABLE KEYS */;
INSERT INTO `teamgroups` VALUES (1,'Group A'),(2,'Group B');
/*!40000 ALTER TABLE `teamgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `TeamID` int NOT NULL,
  `GroupID` int DEFAULT NULL,
  `TeamName` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `Points` int DEFAULT '0',
  `Wins` int DEFAULT '0',
  `Losses` int DEFAULT '0',
  `GF` int DEFAULT '0',
  `GA` int DEFAULT '0',
  `Position` int DEFAULT NULL,
  `Draw` int DEFAULT '0',
  PRIMARY KEY (`TeamID`),
  KEY `GroupID` (`GroupID`),
  CONSTRAINT `teams_ibfk_1` FOREIGN KEY (`GroupID`) REFERENCES `teamgroups` (`GroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES (1,1,'Team A1',0,0,0,0,0,NULL,0),(2,1,'Team A2',0,0,0,0,0,NULL,0),(3,1,'Team A3',0,0,0,0,0,NULL,0),(4,1,'Team A4',0,0,0,0,0,NULL,0),(5,1,'Team A5',0,0,0,0,0,NULL,0),(6,1,'Team A6',0,0,0,0,0,NULL,0),(7,1,'Team A7',0,0,0,0,0,NULL,0),(8,1,'Team A8',0,0,0,0,0,NULL,0),(9,1,'Team A9',0,0,0,0,0,NULL,0),(10,1,'Team A10',0,0,0,0,0,NULL,0),(11,2,'Team B1',0,0,0,0,0,NULL,0),(12,2,'Team B2',0,0,0,0,0,NULL,0),(13,2,'Team B3',0,0,0,0,0,NULL,0),(14,2,'Team B4',0,0,0,0,0,NULL,0),(15,2,'Team B5',0,0,0,0,0,NULL,0),(16,2,'Team B6',0,0,0,0,0,NULL,0),(17,2,'Team B7',0,0,0,0,0,NULL,0),(18,2,'Team B8',0,0,0,0,0,NULL,0),(19,2,'Team B9',0,0,0,0,0,NULL,0),(20,2,'Team B10',0,0,0,0,0,NULL,0);
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-09-08 16:56:47
