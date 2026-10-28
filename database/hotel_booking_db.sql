-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: hotel_booking_db
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `chi_tiet_dat_phong`
--

DROP TABLE IF EXISTS `chi_tiet_dat_phong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chi_tiet_dat_phong` (
  `ma_ctdp` int NOT NULL AUTO_INCREMENT,
  `ma_dp` int NOT NULL,
  `ma_phong` int NOT NULL,
  `gia_phong` decimal(12,2) NOT NULL,
  `so_dem` int NOT NULL,
  `thanh_tien` decimal(12,2) NOT NULL,
  PRIMARY KEY (`ma_ctdp`),
  KEY `ma_dp` (`ma_dp`),
  KEY `ma_phong` (`ma_phong`),
  CONSTRAINT `chi_tiet_dat_phong_ibfk_1` FOREIGN KEY (`ma_dp`) REFERENCES `dat_phong` (`ma_dp`) ON DELETE CASCADE,
  CONSTRAINT `chi_tiet_dat_phong_ibfk_2` FOREIGN KEY (`ma_phong`) REFERENCES `phong` (`ma_phong`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_dat_phong`
--

LOCK TABLES `chi_tiet_dat_phong` WRITE;
/*!40000 ALTER TABLE `chi_tiet_dat_phong` DISABLE KEYS */;
INSERT INTO `chi_tiet_dat_phong` VALUES (1,1,5,800000.00,2,1600000.00),(2,2,6,500000.00,3,1500000.00),(3,3,1,500000.00,21,10500000.00),(4,3,2,500000.00,21,10500000.00),(5,4,4,800000.00,11,8800000.00),(6,5,3,500000.00,3,1500000.00),(7,5,4,800000.00,3,2400000.00),(8,6,5,800000.00,1,800000.00);
/*!40000 ALTER TABLE `chi_tiet_dat_phong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dat_dich_vu`
--

DROP TABLE IF EXISTS `dat_dich_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dat_dich_vu` (
  `ma_ddv` int NOT NULL AUTO_INCREMENT,
  `ma_dp` int NOT NULL,
  `ma_dv` int NOT NULL,
  `so_luong` int NOT NULL DEFAULT '1',
  `don_gia` decimal(12,2) NOT NULL,
  `ngay_su_dung` date DEFAULT NULL,
  `thanh_tien` decimal(12,2) NOT NULL,
  `ma_nv_ghi_nhan` int DEFAULT NULL,
  PRIMARY KEY (`ma_ddv`),
  KEY `ma_dp` (`ma_dp`),
  KEY `ma_dv` (`ma_dv`),
  KEY `ma_nv_ghi_nhan` (`ma_nv_ghi_nhan`),
  CONSTRAINT `dat_dich_vu_ibfk_1` FOREIGN KEY (`ma_dp`) REFERENCES `dat_phong` (`ma_dp`) ON DELETE CASCADE,
  CONSTRAINT `dat_dich_vu_ibfk_2` FOREIGN KEY (`ma_dv`) REFERENCES `dich_vu` (`ma_dv`),
  CONSTRAINT `dat_dich_vu_ibfk_3` FOREIGN KEY (`ma_nv_ghi_nhan`) REFERENCES `tai_khoan` (`ma_tk`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dat_dich_vu`
--

LOCK TABLES `dat_dich_vu` WRITE;
/*!40000 ALTER TABLE `dat_dich_vu` DISABLE KEYS */;
INSERT INTO `dat_dich_vu` VALUES (1,1,2,2,150000.00,'2026-10-02',300000.00,2);
/*!40000 ALTER TABLE `dat_dich_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dat_phong`
--

DROP TABLE IF EXISTS `dat_phong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dat_phong` (
  `ma_dp` int NOT NULL AUTO_INCREMENT,
  `ma_tra_cuu` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_kh` int NOT NULL,
  `ma_nv_xu_ly` int DEFAULT NULL,
  `ngay_dat` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_nhan_phong` date NOT NULL,
  `ngay_tra_phong` date NOT NULL,
  `trang_thai` enum('CHO_XAC_NHAN','DA_XAC_NHAN','DA_NHAN_PHONG','DA_TRA_PHONG','DA_HUY') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_XAC_NHAN',
  `tien_phong` decimal(12,2) NOT NULL DEFAULT '0.00',
  `tien_dich_vu` decimal(12,2) NOT NULL DEFAULT '0.00',
  `tong_tien` decimal(12,2) NOT NULL DEFAULT '0.00',
  `ghi_chu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ma_dp`),
  UNIQUE KEY `ma_tra_cuu` (`ma_tra_cuu`),
  KEY `ma_kh` (`ma_kh`),
  KEY `ma_nv_xu_ly` (`ma_nv_xu_ly`),
  KEY `idx_dp_ngay` (`ngay_nhan_phong`,`ngay_tra_phong`),
  KEY `idx_dp_trangthai` (`trang_thai`),
  KEY `idx_dp_tracuu` (`ma_tra_cuu`),
  CONSTRAINT `dat_phong_ibfk_1` FOREIGN KEY (`ma_kh`) REFERENCES `khach_hang` (`ma_kh`),
  CONSTRAINT `dat_phong_ibfk_2` FOREIGN KEY (`ma_nv_xu_ly`) REFERENCES `tai_khoan` (`ma_tk`),
  CONSTRAINT `dat_phong_chk_1` CHECK ((`ngay_tra_phong` > `ngay_nhan_phong`))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dat_phong`
--

LOCK TABLES `dat_phong` WRITE;
/*!40000 ALTER TABLE `dat_phong` DISABLE KEYS */;
INSERT INTO `dat_phong` VALUES (1,'HTL2610A7X2',1,2,'2026-09-18 11:22:29','2026-10-01','2026-10-03','DA_XAC_NHAN',1600000.00,300000.00,1900000.00,NULL),(2,'HTL2609B3K9',2,2,'2026-09-18 11:22:29','2026-09-15','2026-09-18','DA_NHAN_PHONG',1500000.00,0.00,1500000.00,NULL),(3,'HTL8D3EU9X0',3,1,'2026-10-16 13:31:33','2026-10-01','2026-10-22','DA_TRA_PHONG',21000000.00,0.00,0.00,NULL),(4,'HTLTMLSTVMY',4,NULL,'2026-10-16 13:34:28','2026-10-10','2026-10-21','DA_HUY',8800000.00,0.00,0.00,NULL),(5,'HTLUFGWVNTE',4,NULL,'2026-10-16 13:35:48','2026-10-03','2026-10-06','CHO_XAC_NHAN',3900000.00,0.00,0.00,NULL),(6,'HTLWT0J7EGP',5,NULL,'2026-10-17 01:20:21','2026-10-03','2026-10-04','DA_HUY',800000.00,0.00,0.00,NULL);
/*!40000 ALTER TABLE `dat_phong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dich_vu`
--

DROP TABLE IF EXISTS `dich_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dich_vu` (
  `ma_dv` int NOT NULL AUTO_INCREMENT,
  `ten_dv` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `don_gia` decimal(12,2) NOT NULL,
  `don_vi_tinh` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'lan',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `trang_thai` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ma_dv`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dich_vu`
--

LOCK TABLES `dich_vu` WRITE;
/*!40000 ALTER TABLE `dich_vu` DISABLE KEYS */;
INSERT INTO `dich_vu` VALUES (1,'Dua don san bay',300000.00,'lan','Xe dua don san bay - khach san',1),(2,'An sang buffet',150000.00,'suat','Buffet sang cho 1 nguoi',1),(3,'Giat ui',50000.00,'kg','Giat ui quan ao',1),(4,'Massage - Spa',400000.00,'lan','Massage thu gian 60 phut',1);
/*!40000 ALTER TABLE `dich_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khach_hang`
--

DROP TABLE IF EXISTS `khach_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khach_hang` (
  `ma_kh` int NOT NULL AUTO_INCREMENT,
  `ma_tk` int DEFAULT NULL,
  `ho_ten` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `so_dien_thoai` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ma_kh`),
  KEY `idx_kh_email` (`email`),
  KEY `fk_kh_tk` (`ma_tk`),
  CONSTRAINT `fk_kh_tk` FOREIGN KEY (`ma_tk`) REFERENCES `tai_khoan` (`ma_tk`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach_hang`
--

LOCK TABLES `khach_hang` WRITE;
/*!40000 ALTER TABLE `khach_hang` DISABLE KEYS */;
INSERT INTO `khach_hang` VALUES (1,NULL,'Tran Van Nam','nam@gmail.com','0912345678','079201001234','Quan 1, TP.HCM','2026-09-18 11:22:29'),(2,NULL,'Le Thi Hoa','hoa@gmail.com','0987654321','079201005678','Quan 7, TP.HCM','2026-09-18 11:22:29'),(3,NULL,'aaa','aaaa@gmail.com','2132','','','2026-10-16 13:31:33'),(4,NULL,'aa','nguyenanhtu4537@gmail.com','a','','','2026-10-16 13:34:28'),(5,NULL,'ss','ss@g','s','q23','','2026-10-17 01:20:21');
/*!40000 ALTER TABLE `khach_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loai_phong`
--

DROP TABLE IF EXISTS `loai_phong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loai_phong` (
  `ma_loai` int NOT NULL AUTO_INCREMENT,
  `ten_loai` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `gia_mac_dinh` decimal(12,2) NOT NULL,
  `suc_chua` int NOT NULL DEFAULT '2',
  `dien_tich` decimal(6,2) DEFAULT NULL,
  `hinh_anh` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ma_loai`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loai_phong`
--

LOCK TABLES `loai_phong` WRITE;
/*!40000 ALTER TABLE `loai_phong` DISABLE KEYS */;
INSERT INTO `loai_phong` VALUES (1,'Blossom Suite','Phong am cung, khong gian yen tinh, phu hop cho cap doi va khach cong tac ngan ngay',1450000.00,2,32.00,'/images/rooms/BlossomSuite/Room_6%20%287%29a_6129022bde6a6.jpeg'),(2,'Signature Suites','Phong cao cap, noi that go hien dai, bo cuc rong rai voi khu tiep khach rieng',1850000.00,2,38.00,'/images/rooms/SignatureSuites/Room_3%20%285%29a_6129005564f1a.jpeg'),(3,'Sky Corner Suites','Phong goc cao cap, nhieu anh sang tu nhien va tam nhin thoang',2150000.00,3,42.00,'/images/rooms/SkyCornerSuites/Room_1%20%2814%29a_612901bc99234.jpeg'),(4,'Family Premium Suites','Phong danh cho gia dinh, dien tich lon, day du tien nghi luu tru dai ngay',2650000.00,4,55.00,'/images/rooms/FamilyPremiumSuites/Room_2%20%2816%29a_612902bdd57f2.jpeg');
/*!40000 ALTER TABLE `loai_phong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phong`
--

DROP TABLE IF EXISTS `phong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phong` (
  `ma_phong` int NOT NULL AUTO_INCREMENT,
  `so_phong` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_loai` int NOT NULL,
  `tang` int DEFAULT NULL,
  `trang_thai` enum('TRONG','DA_DAT','DANG_O','BAO_TRI') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TRONG',
  `ghi_chu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ma_phong`),
  UNIQUE KEY `so_phong` (`so_phong`),
  KEY `ma_loai` (`ma_loai`),
  KEY `idx_phong_trangthai` (`trang_thai`),
  CONSTRAINT `phong_ibfk_1` FOREIGN KEY (`ma_loai`) REFERENCES `loai_phong` (`ma_loai`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phong`
--

LOCK TABLES `phong` WRITE;
/*!40000 ALTER TABLE `phong` DISABLE KEYS */;
INSERT INTO `phong` VALUES (1,'101',1,1,'TRONG',NULL),(2,'102',1,1,'TRONG',NULL),(3,'103',1,1,'TRONG',NULL),(4,'201',2,2,'TRONG',NULL),(5,'202',2,2,'DA_DAT',NULL),(6,'203',2,2,'DANG_O',NULL),(7,'301',3,3,'TRONG',NULL),(8,'401',4,4,'BAO_TRI',NULL);
/*!40000 ALTER TABLE `phong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tai_khoan`
--

DROP TABLE IF EXISTS `tai_khoan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tai_khoan` (
  `ma_tk` int NOT NULL AUTO_INCREMENT,
  `ten_dang_nhap` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mat_khau` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ho_ten` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `so_dien_thoai` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `anh_dai_dien` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vai_tro` enum('CUSTOMER','NHANVIEN','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CUSTOMER',
  `provider` enum('LOCAL','GOOGLE','FACEBOOK') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCAL',
  `provider_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_verified` tinyint(1) DEFAULT '0',
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `trang_thai` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ma_tk`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `ten_dang_nhap` (`ten_dang_nhap`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tai_khoan`
--

LOCK TABLES `tai_khoan` WRITE;
/*!40000 ALTER TABLE `tai_khoan` DISABLE KEYS */;
INSERT INTO `tai_khoan` VALUES (1,'admin','$2a$12$hsjCmFsH/Sbw7LLjvXL5SeD/2PkpSJUqYtrHyOp4R9U7m9EZj.2RW','Nguyen Tuong Huyaa','hytu4535@gmail.com','0786026878',NULL,'ADMIN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(2,'nv_lan','$2a$12$5ZGI0jXY.hMaSKNjx9.N7OSWZ.ffJ3XHVsUX9yHcfjBDINHhPUTZW','Nguyen Thi Lanrr','lan@hotel.com','0900000002',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(3,'nv_tuan','123456','Pham Anh Tuan','tuan@hotel.com','0900000003',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(4,'d','$2a$12$b8J/P6hxbiKqLA.DlR1qY.h9EHHqPgKPxcShx4oNc/JnSnRUcYTO6','d','d@gmail.com','',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-10-04 06:19:21',0);
/*!40000 ALTER TABLE `tai_khoan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thanh_toan`
--

DROP TABLE IF EXISTS `thanh_toan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thanh_toan` (
  `ma_tt` int NOT NULL AUTO_INCREMENT,
  `ma_dp` int NOT NULL,
  `so_tien` decimal(12,2) NOT NULL,
  `ngay_thanh_toan` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `phuong_thuc` enum('TIEN_MAT','CHUYEN_KHOAN','VNPAY','MOMO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_giao_dich` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `loai_thanh_toan` enum('DAT_COC','THANH_TOAN_DU','HOAN_TIEN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'THANH_TOAN_DU',
  `ma_nv_thu` int DEFAULT NULL,
  `ghi_chu` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ma_tt`),
  KEY `ma_dp` (`ma_dp`),
  KEY `ma_nv_thu` (`ma_nv_thu`),
  CONSTRAINT `thanh_toan_ibfk_1` FOREIGN KEY (`ma_dp`) REFERENCES `dat_phong` (`ma_dp`) ON DELETE CASCADE,
  CONSTRAINT `thanh_toan_ibfk_2` FOREIGN KEY (`ma_nv_thu`) REFERENCES `tai_khoan` (`ma_tk`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thanh_toan`
--

LOCK TABLES `thanh_toan` WRITE;
/*!40000 ALTER TABLE `thanh_toan` DISABLE KEYS */;
INSERT INTO `thanh_toan` VALUES (1,1,500000.00,'2026-09-18 11:22:29','CHUYEN_KHOAN',NULL,'DAT_COC',2,'Coc giu phong');
/*!40000 ALTER TABLE `thanh_toan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-19  0:07:55
