-- MySQL dump 10.14  Distrib 5.5.49-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: ebegu
-- ------------------------------------------------------
-- Server version	5.5.49-MariaDB-1ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adresse`
--

DROP TABLE IF EXISTS `adresse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adresse` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gemeinde` varchar(255) DEFAULT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `hausnummer` varchar(100) DEFAULT NULL,
  `land` varchar(255) NOT NULL,
  `ort` varchar(255) NOT NULL,
  `plz` varchar(100) NOT NULL,
  `strasse` varchar(255) NOT NULL,
  `zusatzzeile` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adresse`
--

LOCK TABLES `adresse` WRITE;
/*!40000 ALTER TABLE `adresse` DISABLE KEYS */;
INSERT INTO `adresse` VALUES ('8d08e0ea-56b6-4764-93c2-e4d945bbc021','2016-05-31 09:17:36','2016-05-31 09:17:36','anonymous','anonymous',0,NULL,'1000-01-01','9999-12-31','3','PW','Bern','3003','Bahnhofstrasse','Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore'),('9c0157ba-2c2d-49f0-ab61-2363ce18204a','2016-05-31 09:17:36','2016-05-31 09:17:36','anonymous','anonymous',0,NULL,'1000-01-01','9999-12-31','2','PW','Bern','3002','Breitenrain','Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore'),('df293958-1125-4f78-bec3-97df287de9cd','2016-05-31 09:17:36','2016-05-31 09:17:36','anonymous','anonymous',0,NULL,'1000-01-01','9999-12-31','5','PW','Bern','3005','Worbstrasse','Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore'),('efce0c7a-5c7e-4037-818b-c7ee00558884','2016-05-31 09:17:36','2016-05-31 09:17:36','anonymous','anonymous',0,NULL,'1000-01-01','9999-12-31','1','PW','Bern','3001','Länggasse','Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore'),('f63e05b9-e066-4aec-8222-4e15f382fc70','2016-05-31 09:17:36','2016-05-31 09:17:36','anonymous','anonymous',0,NULL,'1000-01-01','9999-12-31','4','PW','Bern','3004','Könizweg','Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');
/*!40000 ALTER TABLE `adresse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `adresse_aud`
--

DROP TABLE IF EXISTS `adresse_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `adresse_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gemeinde` varchar(255) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `hausnummer` varchar(100) DEFAULT NULL,
  `land` varchar(255) DEFAULT NULL,
  `ort` varchar(255) DEFAULT NULL,
  `plz` varchar(100) DEFAULT NULL,
  `strasse` varchar(255) DEFAULT NULL,
  `zusatzzeile` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_adresse_aud_revinfo` (`rev`),
  CONSTRAINT `FK_adresse_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adresse_aud`
--

LOCK TABLES `adresse_aud` WRITE;
/*!40000 ALTER TABLE `adresse_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `adresse_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_property`
--

DROP TABLE IF EXISTS `application_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_property` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_application_property_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_property`
--

LOCK TABLES `application_property` WRITE;
/*!40000 ALTER TABLE `application_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_property` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_property_aud`
--

DROP TABLE IF EXISTS `application_property_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_property_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_application_property_aud_revinfo` (`rev`),
  CONSTRAINT `FK_application_property_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_property_aud`
--

LOCK TABLES `application_property_aud` WRITE;
/*!40000 ALTER TABLE `application_property_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_property_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuung`
--

DROP TABLE IF EXISTS `betreuung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuung` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `betreuungsstatus` varchar(255) NOT NULL,
  `schulpflichtig` bit(1) DEFAULT NULL,
  `institution_stammdaten_id` varchar(36) NOT NULL,
  `kind_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_betreuung_institution_stammdaten_id` (`institution_stammdaten_id`),
  KEY `FK_betreuung_kind_id` (`kind_id`),
  CONSTRAINT `FK_betreuung_kind_id` FOREIGN KEY (`kind_id`) REFERENCES `kind_container` (`id`),
  CONSTRAINT `FK_betreuung_institution_stammdaten_id` FOREIGN KEY (`institution_stammdaten_id`) REFERENCES `institution_stammdaten` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuung`
--

LOCK TABLES `betreuung` WRITE;
/*!40000 ALTER TABLE `betreuung` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuung` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuung_aud`
--

DROP TABLE IF EXISTS `betreuung_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuung_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `betreuungsstatus` varchar(255) DEFAULT NULL,
  `schulpflichtig` bit(1) DEFAULT NULL,
  `institution_stammdaten_id` varchar(36) DEFAULT NULL,
  `kind_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_betreuung_aud_revinfo` (`rev`),
  CONSTRAINT `FK_betreuung_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuung_aud`
--

LOCK TABLES `betreuung_aud` WRITE;
/*!40000 ALTER TABLE `betreuung_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuung_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuungspensum`
--

DROP TABLE IF EXISTS `betreuungspensum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuungspensum` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `pensum` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuungspensum`
--

LOCK TABLES `betreuungspensum` WRITE;
/*!40000 ALTER TABLE `betreuungspensum` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuungspensum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuungspensum_aud`
--

DROP TABLE IF EXISTS `betreuungspensum_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuungspensum_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `pensum` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_betreuungspensum_aud_revinfo` (`rev`),
  CONSTRAINT `FK_betreuungspensum_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuungspensum_aud`
--

LOCK TABLES `betreuungspensum_aud` WRITE;
/*!40000 ALTER TABLE `betreuungspensum_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuungspensum_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuungspensum_container`
--

DROP TABLE IF EXISTS `betreuungspensum_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuungspensum_container` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `betreuung_id` varchar(36) NOT NULL,
  `betreuungspensumgs_id` varchar(36) DEFAULT NULL,
  `betreuungspensumja_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_betreuungspensum_container_betreuung_id` (`betreuung_id`),
  KEY `FK_betreuungspensum_container_betreuungspensum_gs` (`betreuungspensumgs_id`),
  KEY `FK_betreuungspensum_container_betreuungspensum_ja` (`betreuungspensumja_id`),
  CONSTRAINT `FK_betreuungspensum_container_betreuungspensum_ja` FOREIGN KEY (`betreuungspensumja_id`) REFERENCES `betreuungspensum` (`id`),
  CONSTRAINT `FK_betreuungspensum_container_betreuungspensum_gs` FOREIGN KEY (`betreuungspensumgs_id`) REFERENCES `betreuungspensum` (`id`),
  CONSTRAINT `FK_betreuungspensum_container_betreuung_id` FOREIGN KEY (`betreuung_id`) REFERENCES `betreuung` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuungspensum_container`
--

LOCK TABLES `betreuungspensum_container` WRITE;
/*!40000 ALTER TABLE `betreuungspensum_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuungspensum_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `betreuungspensum_container_aud`
--

DROP TABLE IF EXISTS `betreuungspensum_container_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `betreuungspensum_container_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `betreuung_id` varchar(36) DEFAULT NULL,
  `betreuungspensumgs_id` varchar(36) DEFAULT NULL,
  `betreuungspensumja_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_betreuungspensum_container_aud_revinfo` (`rev`),
  CONSTRAINT `FK_betreuungspensum_container_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `betreuungspensum_container_aud`
--

LOCK TABLES `betreuungspensum_container_aud` WRITE;
/*!40000 ALTER TABLE `betreuungspensum_container_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `betreuungspensum_container_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ebegu_parameter`
--

DROP TABLE IF EXISTS `ebegu_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ebegu_parameter` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ebegu_parameter`
--

LOCK TABLES `ebegu_parameter` WRITE;
/*!40000 ALTER TABLE `ebegu_parameter` DISABLE KEYS */;
INSERT INTO `ebegu_parameter` VALUES ('06c1e5d5-48c0-4f2d-af25-251b15de8ceb','2016-06-03 10:16:38','2016-06-03 10:20:59','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PENSUM_TAGESELTERN_MIN','20'),('06c1e5d5-48c0-4f2d-af25-251b15de8cec','2016-06-03 10:16:38','2016-06-03 10:20:59','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PENSUM_TAGESSCHULE_MIN','0'),('1ea4f569-4705-4165-8e00-4d78395817d4','2016-06-03 10:16:34','2016-06-03 10:20:57','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_ANZAHL_TAGE_KANTON','240'),('3ee7d728-551d-4085-989a-846f092fbdf4','2016-06-03 10:16:38','2016-06-03 10:20:57','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PENSUM_KITA_MIN','10'),('4fbd62ed-38db-48fd-9b98-3d3363f23257','2016-06-03 10:16:34','2016-06-03 10:20:57','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PENSUM_TAGI_MIN','60'),('5676f112-e16b-4356-b6d6-6c80082c6de1','2016-06-03 10:16:36','2016-06-03 10:20:55','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_KOSTEN_PRO_STUNDE_MAX','11.91'),('56a7e7db-3503-46b7-b41f-18216c0ec787','2016-06-03 10:16:34','2016-06-03 10:20:57','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_MASSGEBENDES_EINKOMMEN_MIN','42540'),('5de5b68b-127b-487d-b4a4-7c6539d4b3be','2016-06-03 10:16:34','2016-06-03 10:20:58','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_MASSGEBENDES_EINKOMMEN_MAX','158690'),('606551d2-d55b-4407-bf69-c3080fe8c50b','2016-06-03 10:16:11','2016-06-03 10:20:56','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_KOSTEN_PRO_STUNDE_MAX_TAGESELTERN','9.16'),('6c7af5ff-58f7-4e8f-9e70-9009bb1f1b92','2016-06-03 10:16:37','2016-06-03 10:20:58','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_3','3760'),('71a6c200-f249-4796-8784-10376a9fbd67','2016-06-03 10:16:37','2016-06-03 10:20:59','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_4','5900'),('76d45a75-10ab-46a3-ac24-ce1f50268519','2016-06-03 10:16:39','2016-06-03 10:16:39','flyway','flyway',0,'2016-01-01','2016-12-31','PARAM_ABGELTUNG_PRO_TAG_KANTON','107.19'),('97eed21d-5c4e-4883-a2ac-1a0d9f17e5b6','2016-06-03 10:16:38','2016-06-03 10:20:59','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_5','6970'),('990591a5-62d5-4c28-9b86-6bf7f28552ad','2016-06-03 10:16:11','2016-06-03 10:20:56','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_ANZAL_TAGE_MAX_KITA','244'),('9f373467-8a47-4bb1-9764-1399112f9638','2016-06-03 10:16:34','2016-06-03 10:20:58','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_STUNDEN_PRO_TAG_TAGI','7'),('a4732a56-3050-40b6-8238-e668e98e8f18','2016-06-03 10:16:11','2016-06-03 10:20:56','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_STUNDEN_PRO_TAG_MAX_KITA','11.5'),('a5cd4af5-52d0-4831-b596-7f7507aab973','2016-06-03 10:16:11','2016-06-03 10:20:56','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_FIXBETRAG_STADT_PRO_TAG_KITA','7.00'),('c4877e99-c465-42ee-9299-207f9f546dc2','2016-06-03 10:16:39','2016-06-03 10:20:58','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_MAX_TAGE_ABWESENHEIT','30'),('ccda60ff-7d67-4a65-be2c-db03bab1a9d5','2016-06-03 10:16:11','2016-06-03 10:20:56','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_KOSTEN_PRO_STUNDE_MIN','0.75'),('ccdd3efd-e829-4b3c-bc1c-28c8c3a463a2','2016-06-03 10:16:37','2016-06-03 10:20:58','flyway','flyway',0,'2016-08-01','2017-07-31','PARAM_PAUSCHALABZUG_PRO_PERSON_FAMILIENGROESSE_6','7500');
/*!40000 ALTER TABLE `ebegu_parameter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ebegu_parameter_aud`
--

DROP TABLE IF EXISTS `ebegu_parameter_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ebegu_parameter_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_ebeguparameter_aud_revinfo` (`rev`),
  CONSTRAINT `FK_ebeguparameter_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ebegu_parameter_aud`
--

LOCK TABLES `ebegu_parameter_aud` WRITE;
/*!40000 ALTER TABLE `ebegu_parameter_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `ebegu_parameter_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erwerbspensum`
--

DROP TABLE IF EXISTS `erwerbspensum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erwerbspensum` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `pensum` int(11) DEFAULT NULL,
  `gesundheitliche_einschraenkungen` bit(1) NOT NULL,
  `taetigkeit` varchar(255) NOT NULL,
  `zuschlag_zu_erwerbspensum` bit(1) NOT NULL,
  `zuschlagsgrund` varchar(255) DEFAULT NULL,
  `zuschlagsprozent` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erwerbspensum`
--

LOCK TABLES `erwerbspensum` WRITE;
/*!40000 ALTER TABLE `erwerbspensum` DISABLE KEYS */;
/*!40000 ALTER TABLE `erwerbspensum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erwerbspensum_aud`
--

DROP TABLE IF EXISTS `erwerbspensum_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erwerbspensum_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `pensum` int(11) DEFAULT NULL,
  `gesundheitliche_einschraenkungen` bit(1) DEFAULT NULL,
  `taetigkeit` varchar(255) DEFAULT NULL,
  `zuschlag_zu_erwerbspensum` bit(1) DEFAULT NULL,
  `zuschlagsgrund` varchar(255) DEFAULT NULL,
  `zuschlagsprozent` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_erwerbspensum_aud_revinfo` (`rev`),
  CONSTRAINT `FK_erwerbspensum_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erwerbspensum_aud`
--

LOCK TABLES `erwerbspensum_aud` WRITE;
/*!40000 ALTER TABLE `erwerbspensum_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `erwerbspensum_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erwerbspensum_container`
--

DROP TABLE IF EXISTS `erwerbspensum_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erwerbspensum_container` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `erwerbspensumgs_id` varchar(36) DEFAULT NULL,
  `erwerbspensumja_id` varchar(36) DEFAULT NULL,
  `gesuchsteller_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ErwerbspensumContainer_erwerbspensumgs_id` (`erwerbspensumgs_id`),
  KEY `FK_ErwerbspensumContainer_erwerbspensumja_id` (`erwerbspensumja_id`),
  KEY `FK_ErwerbspensumContainer_gesuchsteller_id` (`gesuchsteller_id`),
  CONSTRAINT `FK_ErwerbspensumContainer_gesuchsteller_id` FOREIGN KEY (`gesuchsteller_id`) REFERENCES `gesuchsteller` (`id`),
  CONSTRAINT `FK_ErwerbspensumContainer_erwerbspensumgs_id` FOREIGN KEY (`erwerbspensumgs_id`) REFERENCES `erwerbspensum` (`id`),
  CONSTRAINT `FK_ErwerbspensumContainer_erwerbspensumja_id` FOREIGN KEY (`erwerbspensumja_id`) REFERENCES `erwerbspensum` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erwerbspensum_container`
--

LOCK TABLES `erwerbspensum_container` WRITE;
/*!40000 ALTER TABLE `erwerbspensum_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `erwerbspensum_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `erwerbspensum_container_aud`
--

DROP TABLE IF EXISTS `erwerbspensum_container_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `erwerbspensum_container_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `erwerbspensumgs_id` varchar(36) DEFAULT NULL,
  `erwerbspensumja_id` varchar(36) DEFAULT NULL,
  `gesuchsteller_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_erwerbspensum_container_aud_revinfo` (`rev`),
  CONSTRAINT `FK_erwerbspensum_container_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `erwerbspensum_container_aud`
--

LOCK TABLES `erwerbspensum_container_aud` WRITE;
/*!40000 ALTER TABLE `erwerbspensum_container_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `erwerbspensum_container_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fachstelle`
--

DROP TABLE IF EXISTS `fachstelle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fachstelle` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `behinderungsbestaetigung` bit(1) NOT NULL,
  `beschreibung` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fachstelle`
--

LOCK TABLES `fachstelle` WRITE;
/*!40000 ALTER TABLE `fachstelle` DISABLE KEYS */;
INSERT INTO `fachstelle` VALUES ('1d1dd5db-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Kindesschutzbehörde','Kindesschutzbehörde'),('1d1dddfe-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Amt für Erwachsenen- und Kindesschutz der Stadt Bern','Erwachsenen-&Kindesschutz'),('1d1de134-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Sozialdienst der Stadt Bern','Sozialdienst'),('1d1de323-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Gesundheitsdienst der Stadt Bern','Gesundheitsdienst'),('1d1de5c0-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Erziehungsberatung des Kantons Bern (Bereich Früherziehungsdienst ebenfalls für Kinder mit Behinderung)','Erziehungsberatung Kanton (Soziale Indikation)'),('1d1de839-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'','Erziehungsberatung Kanton (Erweiterte Bedürfnisse)','Erziehungsberatung Kanton (Erweiterte Bedürfnisse)'),('1d1dea0a-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Fachbereich Asyl und Sozialhilfe des Kompetenzzentrums Integration der Stadt Bern','Kompetenzzentrum Integration'),('1d1dedc0-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','SRK','SRK'),('1d1def8c-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','HEKS','HEKS'),('1d1df238-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Caritas','Caritas'),('1d1df3e4-32f1-11e6-8ae4-ccee479414a5','2016-06-15 14:03:04','2016-06-15 14:03:04','anonymous','anonymous',0,'\0','Andere Fachstelle','Andere Fachstelle');
/*!40000 ALTER TABLE `fachstelle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fachstelle_aud`
--

DROP TABLE IF EXISTS `fachstelle_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fachstelle_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `behinderungsbestaetigung` bit(1) DEFAULT NULL,
  `beschreibung` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_fachstelle_aud_revinfo` (`rev`),
  CONSTRAINT `FK_fachstelle_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fachstelle_aud`
--

LOCK TABLES `fachstelle_aud` WRITE;
/*!40000 ALTER TABLE `fachstelle_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `fachstelle_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fall`
--

DROP TABLE IF EXISTS `fall`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fall` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `fall_nummer` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fall_nummer` (`fall_nummer`),
  KEY `IX_fall_fall_nummer` (`fall_nummer`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fall`
--

LOCK TABLES `fall` WRITE;
/*!40000 ALTER TABLE `fall` DISABLE KEYS */;
/*!40000 ALTER TABLE `fall` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fall_aud`
--

DROP TABLE IF EXISTS `fall_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fall_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `fall_nummer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_fall_aud_revinfo` (`rev`),
  CONSTRAINT `FK_fall_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fall_aud`
--

LOCK TABLES `fall_aud` WRITE;
/*!40000 ALTER TABLE `fall_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `fall_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `familiensituation`
--

DROP TABLE IF EXISTS `familiensituation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `familiensituation` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gesuchsteller_kardinalitaet` varchar(255) DEFAULT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `familienstatus` varchar(255) NOT NULL,
  `gemeinsame_steuererklaerung` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `familiensituation`
--

LOCK TABLES `familiensituation` WRITE;
/*!40000 ALTER TABLE `familiensituation` DISABLE KEYS */;
/*!40000 ALTER TABLE `familiensituation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `familiensituation_aud`
--

DROP TABLE IF EXISTS `familiensituation_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `familiensituation_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gesuchsteller_kardinalitaet` varchar(255) DEFAULT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `familienstatus` varchar(255) DEFAULT NULL,
  `gemeinsame_steuererklaerung` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_familiensituation_aud_revinfo` (`rev`),
  CONSTRAINT `FK_familiensituation_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `familiensituation_aud`
--

LOCK TABLES `familiensituation_aud` WRITE;
/*!40000 ALTER TABLE `familiensituation_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `familiensituation_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finanzielle_situation`
--

DROP TABLE IF EXISTS `finanzielle_situation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finanzielle_situation` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `bruttovermoegen` decimal(19,2) DEFAULT NULL,
  `erhaltene_alimente` decimal(19,2) DEFAULT NULL,
  `ersatzeinkommen` decimal(19,2) DEFAULT NULL,
  `familienzulage` decimal(19,2) DEFAULT NULL,
  `geleistete_alimente` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr_minus1` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr_minus2` decimal(19,2) DEFAULT NULL,
  `nettolohn` decimal(19,2) DEFAULT NULL,
  `schulden` decimal(19,2) DEFAULT NULL,
  `selbstaendig` bit(1) NOT NULL,
  `steuererklaerung_ausgefuellt` bit(1) NOT NULL,
  `steuerveranlagung_erhalten` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finanzielle_situation`
--

LOCK TABLES `finanzielle_situation` WRITE;
/*!40000 ALTER TABLE `finanzielle_situation` DISABLE KEYS */;
/*!40000 ALTER TABLE `finanzielle_situation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finanzielle_situation_aud`
--

DROP TABLE IF EXISTS `finanzielle_situation_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finanzielle_situation_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `bruttovermoegen` decimal(19,2) DEFAULT NULL,
  `erhaltene_alimente` decimal(19,2) DEFAULT NULL,
  `ersatzeinkommen` decimal(19,2) DEFAULT NULL,
  `familienzulage` decimal(19,2) DEFAULT NULL,
  `geleistete_alimente` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr_minus1` decimal(19,2) DEFAULT NULL,
  `geschaeftsgewinn_basisjahr_minus2` decimal(19,2) DEFAULT NULL,
  `nettolohn` decimal(19,2) DEFAULT NULL,
  `schulden` decimal(19,2) DEFAULT NULL,
  `selbstaendig` bit(1) DEFAULT NULL,
  `steuererklaerung_ausgefuellt` bit(1) DEFAULT NULL,
  `steuerveranlagung_erhalten` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `fk_finanzielle_situation_aud_revinfo` (`rev`),
  CONSTRAINT `fk_finanzielle_situation_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finanzielle_situation_aud`
--

LOCK TABLES `finanzielle_situation_aud` WRITE;
/*!40000 ALTER TABLE `finanzielle_situation_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `finanzielle_situation_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finanzielle_situation_container`
--

DROP TABLE IF EXISTS `finanzielle_situation_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finanzielle_situation_container` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `jahr` int(11) NOT NULL,
  `finanzielle_situationgs_id` varchar(36) DEFAULT NULL,
  `finanzielle_situationja_id` varchar(36) DEFAULT NULL,
  `finanzielle_situationsv_id` varchar(36) DEFAULT NULL,
  `gesuchsteller_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_finanzielle_situation_container_gesuchsteller` (`gesuchsteller_id`),
  KEY `fk_finanzielle_situation_container_gs` (`finanzielle_situationgs_id`),
  KEY `fk_finanzielle_situation_container_ja` (`finanzielle_situationja_id`),
  KEY `fk_finanzielle_situation_container_sv` (`finanzielle_situationsv_id`),
  CONSTRAINT `fk_finanzielle_situation_container_gesuchsteller` FOREIGN KEY (`gesuchsteller_id`) REFERENCES `gesuchsteller` (`id`),
  CONSTRAINT `fk_finanzielle_situation_container_gs` FOREIGN KEY (`finanzielle_situationgs_id`) REFERENCES `finanzielle_situation` (`id`),
  CONSTRAINT `fk_finanzielle_situation_container_ja` FOREIGN KEY (`finanzielle_situationja_id`) REFERENCES `finanzielle_situation` (`id`),
  CONSTRAINT `fk_finanzielle_situation_container_sv` FOREIGN KEY (`finanzielle_situationsv_id`) REFERENCES `finanzielle_situation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finanzielle_situation_container`
--

LOCK TABLES `finanzielle_situation_container` WRITE;
/*!40000 ALTER TABLE `finanzielle_situation_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `finanzielle_situation_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finanzielle_situation_container_aud`
--

DROP TABLE IF EXISTS `finanzielle_situation_container_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finanzielle_situation_container_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `jahr` int(11) DEFAULT NULL,
  `finanzielle_situationgs_id` varchar(36) DEFAULT NULL,
  `finanzielle_situationja_id` varchar(36) DEFAULT NULL,
  `finanzielle_situationsv_id` varchar(36) DEFAULT NULL,
  `gesuchsteller_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `fk_finanzielle_situation_container_aud_revinfo` (`rev`),
  CONSTRAINT `fk_finanzielle_situation_container_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finanzielle_situation_container_aud`
--

LOCK TABLES `finanzielle_situation_container_aud` WRITE;
/*!40000 ALTER TABLE `finanzielle_situation_container_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `finanzielle_situation_container_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuch`
--

DROP TABLE IF EXISTS `gesuch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuch` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `fall_id` varchar(36) NOT NULL,
  `familiensituation_id` varchar(36) DEFAULT NULL,
  `gesuchsteller1_id` varchar(36) DEFAULT NULL,
  `gesuchsteller2_id` varchar(36) DEFAULT NULL,
  `einkommensverschlechterung` bit(1) DEFAULT NULL,
  `eingangsdatum` date NOT NULL,
  `gesuchsperiode_id` varchar(36) NOT NULL DEFAULT '0621fb5d-a187-5a91-abaf-8a813c4d263a',
  PRIMARY KEY (`id`),
  KEY `FK_gesuch_familiensituation_id` (`familiensituation_id`),
  KEY `FK_gesuch_fall_id` (`fall_id`),
  KEY `FK_gesuch_gesuchsteller1_id` (`gesuchsteller1_id`),
  KEY `FK_gesuch_gesuchsteller2_id` (`gesuchsteller2_id`),
  KEY `FK_antrag_gesuchsperiode_id` (`gesuchsperiode_id`),
  CONSTRAINT `FK_antrag_gesuchsperiode_id` FOREIGN KEY (`gesuchsperiode_id`) REFERENCES `gesuchsperiode` (`id`),
  CONSTRAINT `FK_gesuch_fall_id` FOREIGN KEY (`fall_id`) REFERENCES `fall` (`id`),
  CONSTRAINT `FK_gesuch_familiensituation_id` FOREIGN KEY (`familiensituation_id`) REFERENCES `familiensituation` (`id`),
  CONSTRAINT `FK_gesuch_gesuchsteller1_id` FOREIGN KEY (`gesuchsteller1_id`) REFERENCES `gesuchsteller` (`id`),
  CONSTRAINT `FK_gesuch_gesuchsteller2_id` FOREIGN KEY (`gesuchsteller2_id`) REFERENCES `gesuchsteller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuch`
--

LOCK TABLES `gesuch` WRITE;
/*!40000 ALTER TABLE `gesuch` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuch_aud`
--

DROP TABLE IF EXISTS `gesuch_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuch_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `fall_id` varchar(36) DEFAULT NULL,
  `familiensituation_id` varchar(36) DEFAULT NULL,
  `gesuchsteller1_id` varchar(36) DEFAULT NULL,
  `gesuchsteller2_id` varchar(36) DEFAULT NULL,
  `einkommensverschlechterung` bit(1) DEFAULT NULL,
  `eingangsdatum` date DEFAULT NULL,
  `gesuchsperiode_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_gesuch_aud_revinfo` (`rev`),
  CONSTRAINT `FK_gesuch_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuch_aud`
--

LOCK TABLES `gesuch_aud` WRITE;
/*!40000 ALTER TABLE `gesuch_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuch_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsperiode`
--

DROP TABLE IF EXISTS `gesuchsperiode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsperiode` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `active` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsperiode`
--

LOCK TABLES `gesuchsperiode` WRITE;
/*!40000 ALTER TABLE `gesuchsperiode` DISABLE KEYS */;
INSERT INTO `gesuchsperiode` VALUES ('0621fb5d-a187-5a91-abaf-8a813c4d263a','2016-05-30 16:39:38','2016-05-30 16:39:38','anonymous','anonymous',0,'2016-08-01','2017-07-31','');
/*!40000 ALTER TABLE `gesuchsperiode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsperiode_aud`
--

DROP TABLE IF EXISTS `gesuchsperiode_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsperiode_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_gesuchsperiode_aud_revinfo` (`rev`),
  CONSTRAINT `FK_gesuchsperiode_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsperiode_aud`
--

LOCK TABLES `gesuchsperiode_aud` WRITE;
/*!40000 ALTER TABLE `gesuchsperiode_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuchsperiode_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsteller`
--

DROP TABLE IF EXISTS `gesuchsteller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsteller` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `geburtsdatum` date NOT NULL,
  `geschlecht` varchar(255) NOT NULL,
  `mail` varchar(255) NOT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `nachname` varchar(255) NOT NULL,
  `telefon` varchar(255) DEFAULT NULL,
  `telefon_ausland` varchar(255) DEFAULT NULL,
  `vorname` varchar(255) NOT NULL,
  `zpv_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsteller`
--

LOCK TABLES `gesuchsteller` WRITE;
/*!40000 ALTER TABLE `gesuchsteller` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuchsteller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsteller_adresse`
--

DROP TABLE IF EXISTS `gesuchsteller_adresse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsteller_adresse` (
  `adresse_typ` varchar(255) DEFAULT NULL,
  `id` varchar(36) NOT NULL,
  `gesuchsteller_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gesuchsteller_adresse_gesuchsteller_id` (`gesuchsteller_id`),
  CONSTRAINT `FK_gesuchsteller_adresse_adresse_id` FOREIGN KEY (`id`) REFERENCES `adresse` (`id`),
  CONSTRAINT `FK_gesuchsteller_adresse_gesuchsteller_id` FOREIGN KEY (`gesuchsteller_id`) REFERENCES `gesuchsteller` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsteller_adresse`
--

LOCK TABLES `gesuchsteller_adresse` WRITE;
/*!40000 ALTER TABLE `gesuchsteller_adresse` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuchsteller_adresse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsteller_adresse_aud`
--

DROP TABLE IF EXISTS `gesuchsteller_adresse_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsteller_adresse_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `adresse_typ` varchar(255) DEFAULT NULL,
  `gesuchsteller_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  CONSTRAINT `FK_personen_adresse_aud_revinfo` FOREIGN KEY (`id`, `rev`) REFERENCES `adresse_aud` (`id`, `rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsteller_adresse_aud`
--

LOCK TABLES `gesuchsteller_adresse_aud` WRITE;
/*!40000 ALTER TABLE `gesuchsteller_adresse_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuchsteller_adresse_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gesuchsteller_aud`
--

DROP TABLE IF EXISTS `gesuchsteller_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gesuchsteller_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `geburtsdatum` date DEFAULT NULL,
  `geschlecht` varchar(255) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `nachname` varchar(255) DEFAULT NULL,
  `telefon` varchar(255) DEFAULT NULL,
  `telefon_ausland` varchar(255) DEFAULT NULL,
  `vorname` varchar(255) DEFAULT NULL,
  `zpv_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_gesuchsteller_aud_revinfo` (`rev`),
  CONSTRAINT `FK_gesuchsteller_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gesuchsteller_aud`
--

LOCK TABLES `gesuchsteller_aud` WRITE;
/*!40000 ALTER TABLE `gesuchsteller_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `gesuchsteller_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institution`
--

DROP TABLE IF EXISTS `institution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institution` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `mandant_id` varchar(36) NOT NULL,
  `traegerschaft_id` varchar(36) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_institution_mandant_id` (`mandant_id`),
  KEY `FK_institution_traegerschaft_id` (`traegerschaft_id`),
  CONSTRAINT `FK_institution_mandant_id` FOREIGN KEY (`mandant_id`) REFERENCES `mandant` (`id`),
  CONSTRAINT `FK_institution_traegerschaft_id` FOREIGN KEY (`traegerschaft_id`) REFERENCES `traegerschaft` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institution`
--

LOCK TABLES `institution` WRITE;
/*!40000 ALTER TABLE `institution` DISABLE KEYS */;
INSERT INTO `institution` VALUES ('1339bf45-d69e-4e30-ba40-036238c4e47b','2016-05-19 17:27:23','2016-05-19 17:27:23','anonymous','anonymous',0,'Inst-Imanol','e3736eb8-6eef-40ef-9e52-96ab48d8f220','71cba831-f044-44e2-a8a2-21376da8a959',''),('7ec09189-de4b-4c87-afcc-1f24f55e6dcd','2016-05-19 17:27:32','2016-05-19 17:27:32','anonymous','anonymous',0,'Tagesschule Nussbaumstr','e3736eb8-6eef-40ef-9e52-96ab48d8f220','71cba831-f044-44e2-a8a2-21376da8a959',''),('9253e9b1-9cae-4278-b578-f1ce93306d29','2016-05-19 17:27:32','2016-05-19 17:27:32','anonymous','anonymous',0,'Inst-Juan','e3736eb8-6eef-40ef-9e52-96ab48d8f220','71cba831-f044-44e2-a8a2-21376da8a959','');
/*!40000 ALTER TABLE `institution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institution_aud`
--

DROP TABLE IF EXISTS `institution_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institution_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `mandant_id` varchar(36) DEFAULT NULL,
  `traegerschaft_id` varchar(36) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_institution_aud_revinfo` (`rev`),
  CONSTRAINT `FK_institution_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institution_aud`
--

LOCK TABLES `institution_aud` WRITE;
/*!40000 ALTER TABLE `institution_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `institution_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institution_stammdaten`
--

DROP TABLE IF EXISTS `institution_stammdaten`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institution_stammdaten` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `betreuungsangebot_typ` varchar(255) DEFAULT NULL,
  `iban` varchar(34) DEFAULT NULL,
  `oeffnungsstunden` decimal(19,2) DEFAULT NULL,
  `oeffnungstage` decimal(19,2) DEFAULT NULL,
  `institution_id` varchar(36) NOT NULL,
  `adresse_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_institution_stammdaten_institution_id` (`institution_id`),
  KEY `FK_institutionStammdaten_adresse_id` (`adresse_id`),
  CONSTRAINT `FK_institutionStammdaten_adresse_id` FOREIGN KEY (`adresse_id`) REFERENCES `adresse` (`id`),
  CONSTRAINT `FK_institution_stammdaten_institution_id` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institution_stammdaten`
--

LOCK TABLES `institution_stammdaten` WRITE;
/*!40000 ALTER TABLE `institution_stammdaten` DISABLE KEYS */;
INSERT INTO `institution_stammdaten` VALUES ('0621fb5d-a187-4a91-abaf-87812c4d261a','2016-05-19 17:29:31','2016-05-19 17:29:31','anonymous','anonymous',0,'2015-01-01','2018-01-01','TAGI','ES123456789',12.00,150.00,'1339bf45-d69e-4e30-ba40-036238c4e47b','efce0c7a-5c7e-4037-818b-c7ee00558884'),('480f3e81-59d0-4c2a-9f85-0f9aafbe18fc','2016-05-19 17:29:19','2016-05-19 17:29:19','anonymous','anonymous',0,'2015-01-01','2018-01-01','KITA','CH123456789',12.00,150.00,'1339bf45-d69e-4e30-ba40-036238c4e47b','9c0157ba-2c2d-49f0-ab61-2363ce18204a'),('70564bf2-793b-44ef-ab20-16660e4f7303','2016-05-19 17:30:36','2016-05-19 17:30:36','anonymous','anonymous',0,'2015-01-01','2018-01-01','TAGESELTERN','ES999999999',12.00,150.00,'9253e9b1-9cae-4278-b578-f1ce93306d29','8d08e0ea-56b6-4764-93c2-e4d945bbc021'),('c10405d6-a905-4879-bb38-fca4cbb3f06f','2016-05-19 17:30:12','2016-05-19 17:30:12','anonymous','anonymous',0,'2015-01-01','2018-01-01','TAGI','ES11111111',12.00,150.00,'9253e9b1-9cae-4278-b578-f1ce93306d29','f63e05b9-e066-4aec-8222-4e15f382fc70'),('d01d7843-0ccb-48a0-ae9e-95bba502ecda','2016-05-19 17:30:12','2016-05-19 17:30:12','anonymous','anonymous',0,'2015-01-01','2018-01-01','TAGESSCHULE','CH123456789',12.00,150.00,'7ec09189-de4b-4c87-afcc-1f24f55e6dcd','df293958-1125-4f78-bec3-97df287de9cd');
/*!40000 ALTER TABLE `institution_stammdaten` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `institution_stammdaten_aud`
--

DROP TABLE IF EXISTS `institution_stammdaten_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `institution_stammdaten_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `betreuungsangebot_typ` varchar(255) DEFAULT NULL,
  `iban` varchar(34) DEFAULT NULL,
  `oeffnungsstunden` decimal(19,2) DEFAULT NULL,
  `oeffnungstage` decimal(19,2) DEFAULT NULL,
  `institution_id` varchar(36) DEFAULT NULL,
  `adresse_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_institution_stammdaten_aud_revinfo` (`rev`),
  CONSTRAINT `FK_institution_stammdaten_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `institution_stammdaten_aud`
--

LOCK TABLES `institution_stammdaten_aud` WRITE;
/*!40000 ALTER TABLE `institution_stammdaten_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `institution_stammdaten_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind`
--

DROP TABLE IF EXISTS `kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kind` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `geburtsdatum` date NOT NULL,
  `geschlecht` varchar(255) NOT NULL,
  `nachname` varchar(255) NOT NULL,
  `vorname` varchar(255) NOT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `familien_ergaenzende_betreuung` bit(1) NOT NULL,
  `muttersprache_deutsch` bit(1) DEFAULT NULL,
  `unterstuetzungspflicht` bit(1) DEFAULT NULL,
  `wohnhaft_im_gleichen_haushalt` int(11) NOT NULL,
  `pensum_fachstelle_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kind_pensum_fachstelle_id` (`pensum_fachstelle_id`),
  CONSTRAINT `FK_kind_pensum_fachstelle_id` FOREIGN KEY (`pensum_fachstelle_id`) REFERENCES `pensum_fachstelle` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind`
--

LOCK TABLES `kind` WRITE;
/*!40000 ALTER TABLE `kind` DISABLE KEYS */;
/*!40000 ALTER TABLE `kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind_aud`
--

DROP TABLE IF EXISTS `kind_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kind_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `geburtsdatum` date DEFAULT NULL,
  `geschlecht` varchar(255) DEFAULT NULL,
  `nachname` varchar(255) DEFAULT NULL,
  `vorname` varchar(255) DEFAULT NULL,
  `bemerkungen` varchar(1000) DEFAULT NULL,
  `familien_ergaenzende_betreuung` bit(1) DEFAULT NULL,
  `muttersprache_deutsch` bit(1) DEFAULT NULL,
  `unterstuetzungspflicht` bit(1) DEFAULT NULL,
  `wohnhaft_im_gleichen_haushalt` int(11) DEFAULT NULL,
  `pensum_fachstelle_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_kind_aud_revinfo` (`rev`),
  CONSTRAINT `FK_kind_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind_aud`
--

LOCK TABLES `kind_aud` WRITE;
/*!40000 ALTER TABLE `kind_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `kind_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind_container`
--

DROP TABLE IF EXISTS `kind_container`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kind_container` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gesuch_id` varchar(36) NOT NULL,
  `kindgs_id` varchar(36) DEFAULT NULL,
  `kindja_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kind_container_gesuch_id` (`gesuch_id`),
  KEY `FK_kind_container_kindgs_id` (`kindgs_id`),
  KEY `FK_kind_container_kindja_id` (`kindja_id`),
  CONSTRAINT `FK_kind_container_kindja_id` FOREIGN KEY (`kindja_id`) REFERENCES `kind` (`id`),
  CONSTRAINT `FK_kind_container_gesuch_id` FOREIGN KEY (`gesuch_id`) REFERENCES `gesuch` (`id`),
  CONSTRAINT `FK_kind_container_kindgs_id` FOREIGN KEY (`kindgs_id`) REFERENCES `kind` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind_container`
--

LOCK TABLES `kind_container` WRITE;
/*!40000 ALTER TABLE `kind_container` DISABLE KEYS */;
/*!40000 ALTER TABLE `kind_container` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kind_container_aud`
--

DROP TABLE IF EXISTS `kind_container_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kind_container_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gesuch_id` varchar(36) DEFAULT NULL,
  `kindgs_id` varchar(36) DEFAULT NULL,
  `kindja_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_kind_container_aud_revinfo` (`rev`),
  CONSTRAINT `FK_kind_container_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kind_container_aud`
--

LOCK TABLES `kind_container_aud` WRITE;
/*!40000 ALTER TABLE `kind_container_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `kind_container_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mandant`
--

DROP TABLE IF EXISTS `mandant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mandant` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mandant`
--

LOCK TABLES `mandant` WRITE;
/*!40000 ALTER TABLE `mandant` DISABLE KEYS */;
INSERT INTO `mandant` VALUES ('e3736eb8-6eef-40ef-9e52-96ab48d8f220','2016-04-25 16:22:22','2016-04-25 16:22:26','anonym','anonym',0,'TestMandant');
/*!40000 ALTER TABLE `mandant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mandant_aud`
--

DROP TABLE IF EXISTS `mandant_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mandant_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_mandant_aud_revinfo` (`rev`),
  CONSTRAINT `FK_mandant_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mandant_aud`
--

LOCK TABLES `mandant_aud` WRITE;
/*!40000 ALTER TABLE `mandant_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `mandant_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pensum_fachstelle`
--

DROP TABLE IF EXISTS `pensum_fachstelle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pensum_fachstelle` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `gueltig_ab` date NOT NULL,
  `gueltig_bis` date NOT NULL,
  `pensum` int(11) DEFAULT NULL,
  `fachstelle_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pensum_fachstelle_fachstelle_id` (`fachstelle_id`),
  CONSTRAINT `FK_pensum_fachstelle_fachstelle_id` FOREIGN KEY (`fachstelle_id`) REFERENCES `fachstelle` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pensum_fachstelle`
--

LOCK TABLES `pensum_fachstelle` WRITE;
/*!40000 ALTER TABLE `pensum_fachstelle` DISABLE KEYS */;
/*!40000 ALTER TABLE `pensum_fachstelle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pensum_fachstelle_aud`
--

DROP TABLE IF EXISTS `pensum_fachstelle_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pensum_fachstelle_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `gueltig_ab` date DEFAULT NULL,
  `gueltig_bis` date DEFAULT NULL,
  `pensum` int(11) DEFAULT NULL,
  `fachstelle_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_pensum_fachstelle_aud_revinfo` (`rev`),
  CONSTRAINT `FK_pensum_fachstelle_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pensum_fachstelle_aud`
--

LOCK TABLES `pensum_fachstelle_aud` WRITE;
/*!40000 ALTER TABLE `pensum_fachstelle_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `pensum_fachstelle_aud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revinfo` (
  `rev` int(11) NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revinfo`
--

LOCK TABLES `revinfo` WRITE;
/*!40000 ALTER TABLE `revinfo` DISABLE KEYS */;
/*!40000 ALTER TABLE `revinfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version_rank` int(11) NOT NULL,
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  KEY `schema_version_vr_idx` (`version_rank`),
  KEY `schema_version_ir_idx` (`installed_rank`),
  KEY `schema_version_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_version`
--

LOCK TABLES `schema_version` WRITE;
/*!40000 ALTER TABLE `schema_version` DISABLE KEYS */;
INSERT INTO `schema_version` VALUES (1,1,'0001','createGesuchstellerAndAppProp','SQL','V0001__createGesuchstellerAndAppProp.sql',584916714,'ebegu','2016-06-15 12:03:04',63,1),(2,2,'0002','createFallGesuchAndFamiliensituation','SQL','V0002__createFallGesuchAndFamiliensituation.sql',-2115192757,'ebegu','2016-06-15 12:03:04',64,1),(3,3,'0003','removeEingetrPartnersch','SQL','V0003__removeEingetrPartnersch.sql',-934499463,'ebegu','2016-06-15 12:03:04',1,1),(4,4,'0004','createAndInsertFachstellen','SQL','V0004__createAndInsertFachstellen.sql',-241116472,'ebegu','2016-06-15 12:03:04',16,1),(5,5,'0006','createKind','SQL','V0006__createKind.sql',1914071041,'ebegu','2016-06-15 12:03:04',42,1),(6,6,'0007','finanzielleSituation','SQL','V0007__finanzielleSituation.sql',673905296,'ebegu','2016-06-15 12:03:04',73,1),(7,7,'0008','InstitutionMandantTraegerschaft','SQL','V0008__InstitutionMandantTraegerschaft.sql',-194112227,'ebegu','2016-06-15 12:03:04',72,1),(8,8,'0009','CreatePersonenAdresse','SQL','V0009__CreatePersonenAdresse.sql',-1223952557,'ebegu','2016-06-15 12:03:04',55,1),(9,9,'0010','createPensumFachstellle','SQL','V0010__createPensumFachstellle.sql',604541531,'ebegu','2016-06-15 12:03:04',29,1),(10,10,'0011','createErwerbspensum','SQL','V0011__createErwerbspensum.sql',1786897190,'ebegu','2016-06-15 12:03:04',47,1),(11,11,'0012','finanzielleSituation2','SQL','V0012__finanzielleSituation2.sql',-205228639,'ebegu','2016-06-15 12:03:04',24,1),(12,12,'0013','createBetreuungEntities','SQL','V0013__createBetreuungEntities.sql',-2043108561,'ebegu','2016-06-15 12:03:04',86,1),(13,13,'0014','insertDummyInstitutionen','SQL','V0014__insertDummyInstitutionen.sql',1756866714,'ebegu','2016-06-15 12:03:04',7,1),(14,14,'0015','createGesuchsperiode','SQL','V0015__createGesuchsperiode.sql',-1640993860,'ebegu','2016-06-15 12:03:05',55,1),(15,15,'0016','fallNummer','SQL','V0016__fallNummer.sql',-325686614,'ebegu','2016-06-15 12:03:05',48,1),(16,16,'0017','ebeguParameter','SQL','V0017__ebeguParameter.sql',1648784051,'ebegu','2016-06-15 12:03:05',19,1),(17,17,'0018','correctFKinstTraegerschaft','SQL','V0018__correctFKinstTraegerschaft.sql',-2028977784,'ebegu','2016-06-15 12:03:05',107,1);
/*!40000 ALTER TABLE `schema_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traegerschaft`
--

DROP TABLE IF EXISTS `traegerschaft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traegerschaft` (
  `id` varchar(36) NOT NULL,
  `timestamp_erstellt` datetime NOT NULL,
  `timestamp_mutiert` datetime NOT NULL,
  `user_erstellt` varchar(36) NOT NULL,
  `user_mutiert` varchar(36) NOT NULL,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traegerschaft`
--

LOCK TABLES `traegerschaft` WRITE;
/*!40000 ALTER TABLE `traegerschaft` DISABLE KEYS */;
INSERT INTO `traegerschaft` VALUES ('71cba831-f044-44e2-a8a2-21376da8a959','2016-05-19 17:22:37','2016-05-19 17:22:37','anonymous','anonymous',0,'TEST-Traegerschaft');
/*!40000 ALTER TABLE `traegerschaft` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `traegerschaft_aud`
--

DROP TABLE IF EXISTS `traegerschaft_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `traegerschaft_aud` (
  `id` varchar(36) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `timestamp_erstellt` datetime DEFAULT NULL,
  `timestamp_mutiert` datetime DEFAULT NULL,
  `user_erstellt` varchar(36) DEFAULT NULL,
  `user_mutiert` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK_traegerschaft_aud_revinfo` (`rev`),
  CONSTRAINT `FK_traegerschaft_aud_revinfo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `traegerschaft_aud`
--

LOCK TABLES `traegerschaft_aud` WRITE;
/*!40000 ALTER TABLE `traegerschaft_aud` DISABLE KEYS */;
/*!40000 ALTER TABLE `traegerschaft_aud` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-06-15 14:09:39
