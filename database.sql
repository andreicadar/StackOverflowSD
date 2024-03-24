CREATE DATABASE  IF NOT EXISTS `stackoverflowsd` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `stackoverflowsd`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: stackoverflowsd
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `answer`
--

DROP TABLE IF EXISTS `answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `text` varchar(5000) NOT NULL,
  `creationTime` datetime NOT NULL,
  `picturePath` varchar(400) DEFAULT NULL,
  `score` int DEFAULT '0',
  `questionID` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `questionID_FK_idx` (`questionID`),
  CONSTRAINT `FK_QUESTION_ID` FOREIGN KEY (`questionID`) REFERENCES `question` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answer`
--

LOCK TABLES `answer` WRITE;
/*!40000 ALTER TABLE `answer` DISABLE KEYS */;
INSERT INTO `answer` VALUES (7,3,'Ia uite pe mine m-a ajutat sa sterg cache-ul din windows. Sper ca te ajuta.','2024-03-24 18:44:45','.\\images\\A7U3.jpg',0,16),(8,11,'Eventual mai pune niste RAM in laptop daca mai poti.','2024-03-24 19:10:41','.\\images\\A8U11.jpg',0,16),(9,12,'Such wow, poate notepad este un virus cine stie ce face in spate, zic sa ii dai un scan','2024-03-24 19:48:31','.\\images\\A9U12.png',0,16);
/*!40000 ALTER TABLE `answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `title` varchar(300) NOT NULL,
  `text` varchar(5000) NOT NULL,
  `creationTime` datetime NOT NULL,
  `picturePath` varchar(400) DEFAULT NULL,
  `score` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `FK_USER_ID_2_idx` (`userID`),
  CONSTRAINT `FK_USER_ID_2` FOREIGN KEY (`userID`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (12,3,'spring auth problem','Ba io orice fac numa nu mere spring, iar la colega veverita ii mere tat','2024-03-23 01:06:43','.\\images\\Q12U3Screenshot mail UMD.png',0),(13,3,'nu mere windows','Ba io orice fac numa nu mere spring, iar la colega veverita ii mere tat','2024-03-23 01:08:53','.\\images\\Q13Uarici.png',0),(16,9,'Merge incet laptop-ul','Am ceva laptop avion cu Intel i7, NVIDIA RTX 4090, dar cand pornesc notepad dureaza cam 10 secunde pana porneste, colegul meu arici a zis ca nu stie de ce, dau funda.','2024-03-24 12:59:59','.\\images\\Q16U9.png',-1),(17,9,'Nu tine bateria la laptop','Noa eu nu demult mi-am luat laptop cu Intel i7 la care cica tine bateria da la mine sigur nu tine. Stiti ce as putea face?','2024-03-24 13:00:56','.\\images\\Q17U9.png',1),(18,9,'Nu merge lumina la tastatura','Pe tot acelasi laptop nou, ca deja inebunesc, nu merge nici lumina de la tastatura, dau upvote la top comment','2024-03-24 13:04:18','.\\images\\Q18U9.png',0),(22,10,'Visual Studio 2022 Crash','Am instalat update nou pe visual studio si nu mai merge, oare sa fie ca am dat update la windows deodata?','2024-03-24 13:12:33','.\\images\\Q22U10.png',0),(23,10,'Spring dependency circular','Am tot adaugat dependencies ca indienii de pe youtube si acuma imi da ca is circulare','2024-03-24 13:13:24','.\\images\\Q23U10.png',0),(24,10,'MySQL am uitat parola','Ca tot romanul mi-am uitat parola la baza de date, ce pot face?','2024-03-24 13:14:05','.\\images\\Q24U10.png',0),(25,11,'Anagajare 2 firme','Ce ziceti ma pot anagaja la 2 firme deodata? Am un loc de munca bun acum, dar mai am o oferta de 10k euro pe luna net','2024-03-24 13:15:23','.\\images\\Q25U11.png',0),(26,11,'Merita intelij','Auziti voi ce faceti cand aveti prea multi bani? De ex urmeaza sa am un proiect pe spring si am atatia bani incat chiar mi-as lua intelij. Oare ajuta pentru spring intelij fata de eclipse?','2024-03-24 13:16:55','.\\images\\Q26U11.png',-1),(27,12,'Tehnoclogii back end','Voi ce tehnologii folositi pe back end? Eu momentan folosesc spring ca toata lumea zice ca ii such wow, da nu mi se pare tare fain ca ii totu gata facut gen eu ce mai gandesc? Sriu //copilot fa aia, tac pac tab de 2 ori si am proiectu gata. Am auzit ca la alte tehnologii ai mai mult de gandit ca na nu site copilotu ','2024-03-24 13:19:08','.\\images\\Q27U12.png',0),(28,12,'Cast la int','Cand castez ceva la int imi da o erore ciudata intelij asta, cica cannot cast int, ati mai intalnit?','2024-03-24 13:21:53','.\\images\\Q28U12.png',-1);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_tag_join`
--

DROP TABLE IF EXISTS `question_tag_join`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_tag_join` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `f_key_question_id_idx` (`question_id`),
  KEY `f_key_tag_id_idx` (`tag_id`),
  CONSTRAINT `f_key_question_id` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
  CONSTRAINT `f_key_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_tag_join`
--

LOCK TABLES `question_tag_join` WRITE;
/*!40000 ALTER TABLE `question_tag_join` DISABLE KEYS */;
INSERT INTO `question_tag_join` VALUES (22,12,16),(23,12,17),(24,12,18),(47,16,53),(48,16,54),(49,16,55),(50,17,53),(51,17,57),(52,18,58),(53,18,59),(54,18,60),(55,18,61),(60,22,62),(61,22,37),(62,23,16),(63,24,71),(64,25,72),(65,26,72),(66,26,16),(67,26,75),(68,27,80),(69,27,17),(70,27,16),(71,28,79),(72,28,17),(73,13,48);
/*!40000 ALTER TABLE `question_tag_join` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ID_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (48,'apache'),(18,'authentication'),(57,'baterie'),(80,'copilot'),(53,'intel'),(75,'intelij'),(17,'java'),(55,'lag'),(58,'laptop'),(72,'lifestyle'),(38,'linux'),(60,'lumini'),(71,'mysql'),(54,'nvidia'),(79,'programare'),(59,'rgb'),(16,'spring'),(61,'tastatura'),(62,'visual studio'),(37,'windows');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` varchar(45) NOT NULL,
  `score` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (3,'arici','$2a$10$yenK7LSztXLuSJ4IPxBT0.jzIBcxrDyc0rJOYOO7u0nAZvR55A.TS','ariciTzepos@yahoo.com','ROLE_USER',0),(6,'arici2','$2a$10$/NTxr8XvnZwGToXKXNy2Ruju1FwYNnFKVb8iHPt5RIJOS5EajJb92','ariciTzepos@yahoo.com','ROLE_USER',0),(8,'arici3','$2a$10$aesY4PtI3/uACVTMfG73.OFdOY..Dw.fHweAExYET0z0ZM.zDtZFm','ariciTzepos@yahoo.com','ROLE_USER',0),(9,'veveritzaHackeritza','$2a$10$r939K7DloHe34TcSeA7D.uaLLSaC5GPe.yvMA1BmTBr39uAonR6wO','veveritza1337@yahoo.com','ROLE_USER',0),(10,'iepurilaRila','$2a$10$fDoDH8PGt74nG7Tt29x0d.pi9rp.NfBrXOCOSEx8TdZKd7mbL0cw.','iepurascpp@yahoo.com','ROLE_USER',0),(11,'sobolan','$2a$10$wRABP7QsYDljSA7P6HuHHOloYQ9ZFqZQpjGN4OikYH3E2XYnO6PXy','sobo@yahoo.com','ROLE_USER',0),(12,'dodgeTheDogo','$2a$10$LntDDsI4pcMfzR1XfMbNKOrS02b2YJb3a06NyiGhm5N3xzFCjGvbu','scoobyDoo@yahoo.com','ROLE_USER',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_answer_vote`
--

DROP TABLE IF EXISTS `user_answer_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_answer_vote` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `answerID` int NOT NULL,
  `upvote` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `userID_FK2_idx` (`userID`),
  KEY `answerID_FK3_idx` (`answerID`),
  CONSTRAINT `answerID_FK3` FOREIGN KEY (`answerID`) REFERENCES `answer` (`id`),
  CONSTRAINT `userID_FK2` FOREIGN KEY (`userID`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_answer_vote`
--

LOCK TABLES `user_answer_vote` WRITE;
/*!40000 ALTER TABLE `user_answer_vote` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_answer_vote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_question_vote`
--

DROP TABLE IF EXISTS `user_question_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_question_vote` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `questionID` int NOT NULL,
  `upvote` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `userID_FK_idx` (`userID`),
  KEY `questionID_FK_idx` (`questionID`),
  CONSTRAINT `questionID_FK` FOREIGN KEY (`questionID`) REFERENCES `question` (`id`),
  CONSTRAINT `userID_FK` FOREIGN KEY (`userID`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_question_vote`
--

LOCK TABLES `user_question_vote` WRITE;
/*!40000 ALTER TABLE `user_question_vote` DISABLE KEYS */;
INSERT INTO `user_question_vote` VALUES (8,3,16,-1),(9,3,17,1),(10,3,26,-1),(11,12,28,-1);
/*!40000 ALTER TABLE `user_question_vote` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-24 19:49:31
