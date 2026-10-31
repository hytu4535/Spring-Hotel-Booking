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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chi_tiet_dat_phong`
--

LOCK TABLES `chi_tiet_dat_phong` WRITE;
/*!40000 ALTER TABLE `chi_tiet_dat_phong` DISABLE KEYS */;
INSERT INTO `chi_tiet_dat_phong` VALUES (1,1,5,800000.00,2,1600000.00),(2,2,6,500000.00,3,1500000.00),(3,3,1,500000.00,21,10500000.00),(4,3,2,500000.00,21,10500000.00),(5,4,4,800000.00,11,8800000.00),(6,5,3,500000.00,3,1500000.00),(7,5,4,800000.00,3,2400000.00),(8,6,5,800000.00,1,800000.00),(9,7,3,500000.00,14,7000000.00),(11,24,2,500000.00,2,1000000.00),(12,25,9,500000.00,6,3000000.00),(13,26,4,800000.00,14,11200000.00),(14,27,4,800000.00,20,16000000.00);
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dat_phong`
--

LOCK TABLES `dat_phong` WRITE;
/*!40000 ALTER TABLE `dat_phong` DISABLE KEYS */;
INSERT INTO `dat_phong` VALUES (1,'HTL2610A7X2',1,2,'2026-09-18 11:22:29','2026-10-01','2026-10-03','DA_XAC_NHAN',1600000.00,300000.00,1900000.00,NULL),(2,'HTL2609B3K9',2,2,'2026-09-18 11:22:29','2026-09-15','2026-09-18','DA_NHAN_PHONG',1500000.00,0.00,1500000.00,NULL),(3,'HTL8D3EU9X0',3,1,'2026-10-16 13:31:33','2026-10-01','2026-10-22','DA_XAC_NHAN',21000000.00,0.00,0.00,NULL),(4,'HTLTMLSTVMY',4,NULL,'2026-10-16 13:34:28','2026-10-10','2026-10-21','DA_HUY',8800000.00,0.00,0.00,NULL),(5,'HTLUFGWVNTE',4,1,'2026-10-16 13:35:48','2026-10-03','2026-10-06','DA_HUY',3900000.00,0.00,0.00,NULL),(6,'HTLWT0J7EGP',5,1,'2026-10-17 01:20:21','2026-10-03','2026-10-04','DA_HUY',800000.00,0.00,0.00,NULL),(7,'HTL7IE04OQ3',7,1,'2026-10-21 12:17:55','2026-10-09','2026-10-23','DA_XAC_NHAN',7000000.00,0.00,0.00,NULL),(24,'HTLT3VPTISX',10,NULL,'2026-10-24 02:39:27','2026-12-01','2026-12-03','CHO_XAC_NHAN',1000000.00,0.00,1000000.00,NULL),(25,'HTLJTJ13R3O',11,1,'2026-10-24 02:47:42','2026-10-08','2026-10-14','DA_XAC_NHAN',3000000.00,0.00,3000000.00,NULL),(26,'HTLPPOTFWA7',6,NULL,'2026-10-24 02:51:17','2026-10-09','2026-10-23','DA_HUY',11200000.00,0.00,11200000.00,NULL),(27,'HTLHCFEDJIN',6,NULL,'2026-10-24 03:53:58','2026-10-09','2026-10-29','CHO_XAC_NHAN',16000000.00,0.00,16000000.00,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dich_vu`
--

LOCK TABLES `dich_vu` WRITE;
/*!40000 ALTER TABLE `dich_vu` DISABLE KEYS */;
INSERT INTO `dich_vu` VALUES (1,'Dua don san bay',300000.00,'lan','Xe dua don san bay - khach san',1),(2,'An sang buffet',150000.00,'suat','Buffet sang cho 1 nguoi',1),(3,'Giat ui',50000.00,'kg','Giat ui quan ao',1),(4,'Massage - Spa',400000.00,'lan','Massage thu gian 60 phut',1),(5,'11',32.00,'','ddd',1);
/*!40000 ALTER TABLE `dich_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `he_thong_quyen`
--

DROP TABLE IF EXISTS `he_thong_quyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `he_thong_quyen` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ma_quyen` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ten_hien_thi` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mau_url` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_41adhueq6dvu5m9e5n9arrcnq` (`ma_quyen`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `he_thong_quyen`
--

LOCK TABLES `he_thong_quyen` WRITE;
/*!40000 ALTER TABLE `he_thong_quyen` DISABLE KEYS */;
INSERT INTO `he_thong_quyen` VALUES (18,'DASHBOARD','Xem tổng quan và dashboard nghiệp vụ','Dashboard','DASHBOARD'),(19,'BOOKING_MANAGE','Xem, xác nhận và cập nhật đơn đặt phòng','Quản lý đặt phòng','BOOKING_MANAGE'),(20,'CHECKIN_OUT','Xem sơ đồ phòng và thực hiện check-in, check-out','Nhận / trả phòng','CHECKIN_OUT'),(21,'SERVICE_PAYMENT','Ghi nhận dịch vụ phát sinh và thanh toán','Dịch vụ & thanh toán','SERVICE_PAYMENT'),(22,'ROOM_MANAGE','Thêm, sửa, xóa và cập nhật trạng thái phòng','Quản lý phòng','ROOM_MANAGE'),(23,'ROOM_TYPE_MANAGE','Quản lý danh mục loại phòng và giá','Quản lý loại phòng','ROOM_TYPE_MANAGE'),(24,'SERVICE_MANAGE','Quản lý danh mục dịch vụ khách sạn','Quản lý dịch vụ','SERVICE_MANAGE'),(25,'CUSTOMER_MANAGE','Quản lý hồ sơ và lịch sử khách hàng','Quản lý khách hàng','CUSTOMER_MANAGE'),(26,'ACCOUNT_MANAGE','Quản lý tài khoản nhân viên và trạng thái tài khoản','Quản lý tài khoản','ACCOUNT_MANAGE'),(27,'ROLE_MANAGE','Thiết lập quyền truy cập chức năng','Quản lý vai trò','ROLE_MANAGE');
/*!40000 ALTER TABLE `he_thong_quyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `he_thong_vai_tro`
--

DROP TABLE IF EXISTS `he_thong_vai_tro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `he_thong_vai_tro` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trang_thai` bit(1) NOT NULL,
  `ma_vai_tro` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ten_hien_thi` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ljrc5vj1vu8855glyrh5g1ag0` (`ma_vai_tro`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `he_thong_vai_tro`
--

LOCK TABLES `he_thong_vai_tro` WRITE;
/*!40000 ALTER TABLE `he_thong_vai_tro` DISABLE KEYS */;
INSERT INTO `he_thong_vai_tro` VALUES (1,_binary '','ADMIN','Toàn quyền quản trị hệ thống','Quản trị viên'),(2,_binary '','NHANVIEN','Vận hành nghiệp vụ khách sạn','Nhân viên'),(3,_binary '','CUSTOMER','Đặt phòng và quản lý thông tin cá nhân','Khách hàng');
/*!40000 ALTER TABLE `he_thong_vai_tro` ENABLE KEYS */;
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
  `so_dien_thoai` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dia_chi` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ma_kh`),
  UNIQUE KEY `uk_khach_hang_ma_tk` (`ma_tk`),
  UNIQUE KEY `uk_khach_hang_so_dien_thoai` (`so_dien_thoai`),
  UNIQUE KEY `uk_khach_hang_cccd` (`cccd`),
  KEY `idx_kh_email` (`email`),
  CONSTRAINT `fk_kh_tk` FOREIGN KEY (`ma_tk`) REFERENCES `tai_khoan` (`ma_tk`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach_hang`
--

LOCK TABLES `khach_hang` WRITE;
/*!40000 ALTER TABLE `khach_hang` DISABLE KEYS */;
INSERT INTO `khach_hang` VALUES (1,NULL,'Tran Van Nam','nam@gmail.com','0912345678','079201001234','Quan 1, TP.HCM','2026-09-18 11:22:29'),(2,NULL,'Le Thi Hoa','hoa@gmail.com','0987654321','079201005678','Quan 7, TP.HCM','2026-09-18 11:22:29'),(3,NULL,'aaa','aaaa@gmail.com','2132',NULL,NULL,'2026-10-16 13:31:33'),(4,NULL,'aa','nguyenanhtu4537@gmail.com','a',NULL,NULL,'2026-10-16 13:34:28'),(5,NULL,'ss','ss@g','s','q23',NULL,'2026-10-17 01:20:21'),(6,6,'Minh Thành','nguyenanhtu4539@gmail.com','0786026878','123456789','aaa','2026-10-20 15:44:17'),(7,NULL,'aaa','aa@gmail.com','afas','','','2026-10-21 12:17:55'),(10,NULL,'Test Guest 2','test-guest-2@example.com','0900000001',NULL,NULL,'2026-10-24 02:39:27'),(11,NULL,'Minh Thành','aaaaaaa@gmail.com','23',NULL,NULL,'2026-10-24 02:47:42');
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loai_phong`
--

LOCK TABLES `loai_phong` WRITE;
/*!40000 ALTER TABLE `loai_phong` DISABLE KEYS */;
INSERT INTO `loai_phong` VALUES (1,'Standardf','Phong tieu chuan, 1 giuong doi, may lanh, TV',500000.00,2,20.00,'standard.jpg'),(2,'Deluxe','Phong cao cap, view thanh pho, minibar',800000.00,2,28.00,'deluxe.jpg'),(3,'Suite','Phong hang sang, co phong khach rieng',1500000.00,4,45.00,'suite.jpg');
/*!40000 ALTER TABLE `loai_phong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ma_tk` int NOT NULL,
  `token_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` datetime NOT NULL,
  `used_at` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_password_reset_token_hash` (`token_hash`),
  KEY `idx_password_reset_token_account` (`ma_tk`),
  KEY `idx_password_reset_token_expiry` (`expires_at`),
  CONSTRAINT `fk_password_reset_token_account` FOREIGN KEY (`ma_tk`) REFERENCES `tai_khoan` (`ma_tk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phong`
--

LOCK TABLES `phong` WRITE;
/*!40000 ALTER TABLE `phong` DISABLE KEYS */;
INSERT INTO `phong` VALUES (1,'101',1,1,'DA_DAT',NULL),(2,'102',1,1,'DA_DAT',NULL),(3,'103',1,1,'DA_DAT',NULL),(4,'201',2,2,'TRONG',NULL),(5,'202',2,2,'TRONG',NULL),(6,'203',2,2,'DANG_O',NULL),(7,'301',3,3,'TRONG',NULL),(8,'302',3,3,'BAO_TRI',NULL),(9,'11',1,11,'DA_DAT','11');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tai_khoan`
--

LOCK TABLES `tai_khoan` WRITE;
/*!40000 ALTER TABLE `tai_khoan` DISABLE KEYS */;
INSERT INTO `tai_khoan` VALUES (1,'admin','$2a$12$hsjCmFsH/Sbw7LLjvXL5SeD/2PkpSJUqYtrHyOp4R9U7m9EZj.2RW','Nguyen Tuong Huyaa','hytu4535@gmail.com','0786026878',NULL,'ADMIN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(2,'nv_lan','$2a$12$5ZGI0jXY.hMaSKNjx9.N7OSWZ.ffJ3XHVsUX9yHcfjBDINHhPUTZW','Nguyen Thi Lan','lan@hotel.com','0900000002',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(3,'nv_tuan','123456','Pham Anh Tuan','tuan@hotel.com','0900000003',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-09-18 11:22:29',1),(4,'d','$2a$12$b8J/P6hxbiKqLA.DlR1qY.h9EHHqPgKPxcShx4oNc/JnSnRUcYTO6','d','d@gmail.com','',NULL,'NHANVIEN','LOCAL',NULL,0,'2026-10-04 06:19:21',1),(5,'google_117831562585358196557','$2a$12$/RBCv/63t1eOBPkxtDc.BuGvWuVJYqV9.A4yFCemSuYEPgvBmqhfe','HyTu','nguyenanhtu4537@gmail.com',NULL,'https://lh3.googleusercontent.com/a/ACg8ocJpD_KDUBA-TnI6lmygu11pMlCjgzT6guaZtMs3SOckzEYDVRr0=s96-c','CUSTOMER','GOOGLE','117831562585358196557',0,'2026-10-20 14:57:30',1),(6,'facebook_1111948838262778','$2a$12$K2nqX4TxXai8iycKoYveLOgxf.Opn8GtINyaFqsBfeHgPAnhrDSWy','Minh Thành','nguyenanhtu4539@gmail.com','0786026878',NULL,'CUSTOMER','FACEBOOK','1111948838262778',0,'2026-10-20 15:43:54',1);
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

--
-- Table structure for table `vai_tro_quyen`
--

DROP TABLE IF EXISTS `vai_tro_quyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vai_tro_quyen` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quyen_id` int NOT NULL,
  `vai_tro_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKb452c0kqydiuwym6gp67myv8e` (`vai_tro_id`,`quyen_id`),
  KEY `FKbdjtt2a1rnqmm14oc9341aa7e` (`quyen_id`),
  CONSTRAINT `FKbdjtt2a1rnqmm14oc9341aa7e` FOREIGN KEY (`quyen_id`) REFERENCES `he_thong_quyen` (`id`),
  CONSTRAINT `FKmxynhmftghkyt8n3rpfraiygv` FOREIGN KEY (`vai_tro_id`) REFERENCES `he_thong_vai_tro` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vai_tro_quyen`
--

LOCK TABLES `vai_tro_quyen` WRITE;
/*!40000 ALTER TABLE `vai_tro_quyen` DISABLE KEYS */;
INSERT INTO `vai_tro_quyen` VALUES (39,18,1),(40,19,1),(41,20,1),(42,21,1),(43,22,1),(44,23,1),(45,24,1),(46,25,1),(47,26,1),(48,27,1),(69,18,2),(66,19,2),(67,20,2),(74,21,2),(71,22,2),(72,23,2),(73,24,2),(68,25,2),(65,26,2),(70,27,2);
/*!40000 ALTER TABLE `vai_tro_quyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khuyen_mai`
--

DROP TABLE IF EXISTS `khuyen_`mai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khuyen_mai` (
  `ma_km` int NOT NULL AUTO_INCREMENT,
  `ma_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ten_km` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phan_tram_giam` decimal(5,2) NOT NULL DEFAULT '0.00',
  `ngay_bat_dau` date NOT NULL,
  `ngay_ket_thuc` date NOT NULL,
  `dieu_kien_toi_thieu` decimal(12,2) DEFAULT '0.00',
  `trang_thai` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ma_km`),
  UNIQUE KEY `ma_code` (`ma_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khuyen_mai`
--

LOCK TABLES `khuyen_mai` WRITE;
/*!40000 ALTER TABLE `khuyen_mai` DISABLE KEYS */;
INSERT INTO `khuyen_mai` VALUES (1,'SUMMER2026','Giam gia mua he 10%',10.00,'2026-06-01','2026-12-31',1000000.00,1),(2,'WELCOME','Uu dai chao mung khach moi 15%',15.00,'2026-01-01','2026-12-31',500000.00,1);
/*!40000 ALTER TABLE `khuyen_mai` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danh_gia`
--

DROP TABLE IF EXISTS `danh_gia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danh_gia` (
  `ma_dg` int NOT NULL AUTO_INCREMENT,
  `ma_dp` int NOT NULL,
  `ma_kh` int NOT NULL,
  `so_sao` int NOT NULL,
  `noi_dung` text COLLATE utf8mb4_unicode_ci,
  `ngay_danh_gia` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ma_dg`),
  KEY `ma_dp` (`ma_dp`),
  KEY `ma_kh` (`ma_kh`),
  CONSTRAINT `danh_gia_ibfk_1` FOREIGN KEY (`ma_dp`) REFERENCES `dat_phong` (`ma_dp`) ON DELETE CASCADE,
  CONSTRAINT `danh_gia_ibfk_2` FOREIGN KEY (`ma_kh`) REFERENCES `khach_hang` (`ma_kh`) ON DELETE CASCADE,
  CONSTRAINT `danh_gia_chk_sao` CHECK ((`so_sao` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danh_gia`
--

LOCK TABLES `danh_gia` WRITE;
/*!40000 ALTER TABLE `danh_gia` DISABLE KEYS */;
INSERT INTO `danh_gia` VALUES (1,3,3,5,'Phong rat sach se, nhan vien phuc vu nhiet tinh chu dao. Rat hai long!','2026-10-22 10:00:00');
/*!40000 ALTER TABLE `danh_gia` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-24 10:56:47
