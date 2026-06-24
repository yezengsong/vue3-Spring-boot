-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: web_novel
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark` (
  `bookmark_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛 ID',
  `novel_id` bigint NOT NULL COMMENT '灏忚 ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`bookmark_id`),
  UNIQUE KEY `uk_user_novel` (`user_id`,`novel_id`),
  KEY `novel_id` (`novel_id`),
  CONSTRAINT `bookmark_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `bookmark_ibfk_2` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`novel_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鏀惰棌琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark`
--

LOCK TABLES `bookmark` WRITE;
/*!40000 ALTER TABLE `bookmark` DISABLE KEYS */;
/*!40000 ALTER TABLE `bookmark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍒嗙被鍚嶇О',
  `sort` int DEFAULT '0' COMMENT '鎺掑簭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灏忚鍒嗙被琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'玄幻奇幻',1,'2026-06-11 17:50:21'),(2,'武侠仙侠',2,'2026-06-11 17:50:21'),(3,'都市言情',3,'2026-06-11 17:50:21'),(4,'历史军事',4,'2026-06-11 17:50:21'),(5,'科幻灵异',5,'2026-06-11 17:50:21'),(6,'网游竞技',6,'2026-06-11 17:50:21'),(7,'女生频道',7,'2026-06-11 17:50:21');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chapter`
--

DROP TABLE IF EXISTS `chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter` (
  `chapter_id` bigint NOT NULL AUTO_INCREMENT,
  `novel_id` bigint NOT NULL COMMENT '灏忚 ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绔犺妭鏍囬',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绔犺妭鍐呭',
  `order_num` int NOT NULL COMMENT '绔犺妭椤哄簭',
  `word_count` int DEFAULT '0' COMMENT '瀛楁暟',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`chapter_id`),
  KEY `idx_novel_order` (`novel_id`,`order_num`),
  CONSTRAINT `chapter_ibfk_1` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`novel_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绔犺妭琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chapter`
--

LOCK TABLES `chapter` WRITE;
/*!40000 ALTER TABLE `chapter` DISABLE KEYS */;
INSERT INTO `chapter` VALUES (1,1,'第一章 陨落的天才','斗气大陆，幅员辽阔，人口众多。在这片大陆上，有着一种名为斗气的神秘力量，这种力量，能够让人拥有移山填海的恐怖实力。斗气大陆，以武为尊，强者为尊。在这里，实力才是硬道理，没有实力，就只能被人踩在脚下。萧炎，萧家历史上空前绝后的斗气修炼天才。四岁就开始修炼斗之气，十岁拥有九段斗之气，十一岁突破十段斗之气，成功凝聚斗之气旋，一跃成为家族百年之内最年轻的斗者。然而，这一切都在他十二岁那年画上了句号。原本天赋异禀的他，在某一天突然失去了所有的斗气，从天才变成了废柴。这一变，就是三年。三年时间，足以让一个天才变成废物，也足以让一个骄傲的少年变得沉默寡言。',1,2500,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(2,1,'第二章 神秘老者','听到这道声音，萧炎猛地一惊，警惕地环顾四周。别找了，老夫在你手上的戒指里。那道苍老的声音再次响起。萧炎低头看向自己手上的黑色戒指，这枚戒指是他母亲留给他的遗物，一直以来他都视若珍宝。你是？萧炎疑惑地问道。嘿嘿，小子，老夫名药尘，你可以叫我药老。那道声音笑道，这三年来，你的斗气都是被老夫吸走的。什么？萧炎大怒，是你害我变成这样的？别生气嘛，小子。药老不以为意，老夫这是在帮你，若不是老夫，你的身体早就承受不住了。',2,2600,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(3,1,'第三章 开始修炼','有了药老的指导，萧炎的修炼之路终于走上了正轨。药老告诉萧炎，斗气修炼，分为斗之气、斗者、斗师、大斗师、斗灵、斗王、斗皇、斗宗、斗尊、斗圣、斗帝十一个大境界。每个境界又分为一到九段。从今日起，老夫便收你为徒，教你真正的修炼之法。',3,2700,'2026-06-11 17:50:21','2026-06-22 17:35:44');
/*!40000 ALTER TABLE `chapter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_id` bigint NOT NULL COMMENT '绔犺妭 ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛 ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '璇勮鍐呭',
  `parent_id` bigint DEFAULT NULL COMMENT '鐖惰瘎璁� ID锛堜簩绾ц瘎璁猴級',
  `like_count` int DEFAULT '0' COMMENT '鐐硅禐鏁�',
  `status` tinyint DEFAULT '0' COMMENT '瀹℃牳鐘舵�侊細0-寰呭鏍� 1-宸查�氳繃 2-宸叉嫆缁�',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`comment_id`),
  KEY `user_id` (`user_id`),
  KEY `parent_id` (`parent_id`),
  KEY `idx_chapter` (`chapter_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`chapter_id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='璇勮琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,2,'写的真好，期待后续！',NULL,10,1,'2026-06-11 17:50:21'),(2,1,3,'土豆大神 YYDS！',NULL,5,1,'2026-06-11 17:50:21'),(3,2,2,'药老终于出场了！',NULL,8,1,'2026-06-11 17:50:21'),(4,2,3,'这个情节很经典啊',2,3,1,'2026-06-11 17:50:21'),(8,1,1,'test',NULL,0,1,'2026-06-11 19:06:11'),(9,1,2,'1',NULL,1,1,'2026-06-11 19:07:46'),(10,2,1,'tt',3,0,1,'2026-06-11 19:25:23'),(11,2,1,'22',3,0,1,'2026-06-11 19:26:21'),(12,1,1,'1',9,3,1,'2026-06-11 19:29:57'),(13,1,1,'22',9,0,1,'2026-06-11 19:32:45'),(14,1,1,'test',NULL,0,1,'2026-06-11 19:33:00'),(15,1,1,'22',NULL,0,1,'2026-06-22 16:58:56'),(16,1,1,'1',15,0,1,'2026-06-22 16:59:00');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `novel`
--

DROP TABLE IF EXISTS `novel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `novel` (
  `novel_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '灏忚鏍囬',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浣滆��',
  `cover` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '灏侀潰 URL',
  `category_id` int DEFAULT NULL COMMENT '鍒嗙被 ID',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '绠�浠�',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵�侊細0-涓嬫灦 1-杩炶浇 2-瀹岀粨',
  `word_count` bigint DEFAULT '0' COMMENT '鎬诲瓧鏁�',
  `click_count` bigint DEFAULT '0' COMMENT '鐐瑰嚮閲�',
  `bookmark_count` int DEFAULT '0' COMMENT '鏀惰棌鏁�',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`novel_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_click` (`click_count`),
  CONSTRAINT `novel_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='灏忚琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `novel`
--

LOCK TABLES `novel` WRITE;
/*!40000 ALTER TABLE `novel` DISABLE KEYS */;
INSERT INTO `novel` VALUES (1,'斗破苍穹','天蚕土豆','/image/斗破苍穹.png',1,'讲述了天才少年萧炎在创造了家族空前绝后的修炼纪录后突然成了废人，整整三年时间，家族冷遇，旁人轻视，被未婚妻退婚种种打击接踵而至。就在他即将绝望的时候，一缕幽魂从他手上的戒指里浮现，一扇全新的大门在面前开启！',2,5300000,100000,5000,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(2,'完美世界','辰东','/image/完美世界.jpg',1,'一粒尘可填海，一根草斩尽日月星辰，弹指间天崩地裂。问世间何为最强？',2,6500000,95000,4800,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(3,'凡人修仙传','忘语','/image/凡人修仙传.jpg',2,'一个普通的山村穷小子，偶然之下，跨入到一个江湖小门派，虽然曾经是天才，但是资质平庸，进入门派之后，进展缓慢。但是他有一颗坚定的心，最终通过自己的努力，成为了一个修仙者。',2,7200000,88000,4500,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(4,'全职高手','蝴蝶蓝','/image/全职高手.jpg',6,'网游荣耀中被誉为教科书级别的顶尖高手，因为种种原因遭到俱乐部的驱逐，离开职业圈的他寄身于一家网吧成了一个小小的网管，但是，拥有十年游戏经验的他，在荣耀新开的第十区重新投入了游戏',2,5800000,92000,4600,'2026-06-11 17:50:21','2026-06-22 17:35:44'),(5,'诡秘之主','爱潜水的乌贼','/image/诡秘之主.png',5,'蒸汽与机械的浪潮中，谁能触及非凡？历史和黑暗的迷雾里，又是谁在耳语？我从诡秘中醒来，睁眼看见这个世界！',2,6800000,98000,5200,'2026-06-11 17:50:21','2026-06-22 17:35:44');
/*!40000 ALTER TABLE `novel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `read_history`
--

DROP TABLE IF EXISTS `read_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `read_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛 ID',
  `novel_id` bigint NOT NULL COMMENT '灏忚 ID',
  `chapter_id` bigint NOT NULL COMMENT '绔犺妭 ID',
  `read_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '闃呰鏃堕棿',
  PRIMARY KEY (`history_id`),
  KEY `novel_id` (`novel_id`),
  KEY `chapter_id` (`chapter_id`),
  KEY `idx_user_time` (`user_id`,`read_time`),
  CONSTRAINT `read_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `read_history_ibfk_2` FOREIGN KEY (`novel_id`) REFERENCES `novel` (`novel_id`) ON DELETE CASCADE,
  CONSTRAINT `read_history_ibfk_3` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`chapter_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='闃呰鍘嗗彶琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `read_history`
--

LOCK TABLES `read_history` WRITE;
/*!40000 ALTER TABLE `read_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `read_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鐢ㄦ埛鍚�',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '瀵嗙爜锛堝姞瀵嗭級',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '閭',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '澶村儚 URL',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'USER' COMMENT '瑙掕壊锛歎SER/ADMIN',
  `status` tinyint DEFAULT '1' COMMENT '鐘舵�侊細0-绂佺敤 1-姝ｅ父',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鐢ㄦ埛琛�';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','$2b$10$alY0bQLW8mjZxMpH10U8nuYraBSNuxV1QFKABQ.FJLYC33/10M.j6','admin@example.com','/image/default-avatar.png','ADMIN',1,'2026-06-11 17:50:21'),(2,'user1','$2b$10$4kJdGKwsdAOODWqQ.hAd/uSbwExPluoox92eULOH0tlprq/Ch8xnq','user1@example.com','/image/default-avatar.png','USER',1,'2026-06-11 17:50:21'),(3,'user2','$2b$10$4kJdGKwsdAOODWqQ.hAd/uSbwExPluoox92eULOH0tlprq/Ch8xnq','user2@example.com','/image/default-avatar.png','USER',1,'2026-06-11 17:50:21');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-22 17:37:11
