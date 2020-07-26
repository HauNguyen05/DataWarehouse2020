/*
 Navicat Premium Data Transfer

 Source Server         : sinhvien
 Source Server Type    : MySQL
 Source Server Version : 100413
 Source Host           : localhost:3306
 Source Schema         : warehouse_main

 Target Server Type    : MySQL
 Target Server Version : 100413
 File Encoding         : 65001

 Date: 20/07/2020 22:10:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for monhoc
-- ----------------------------
DROP TABLE IF EXISTS `monhoc`;
CREATE TABLE `monhoc`  (
  `SK_MH` int(255) NOT NULL AUTO_INCREMENT,
  `STT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Ma_MH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Ten_MH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Tin_Chi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Khoa_QL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Ghi_Chu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `date_expired` date NULL DEFAULT '9999-12-31',
  `date_change` date NULL DEFAULT '9999-12-31',
  PRIMARY KEY (`SK_MH`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warehouse_student
-- ----------------------------
DROP TABLE IF EXISTS `sinhvien`;
CREATE TABLE `sinhvien`  (
  `SK_SV` int(11) NOT NULL AUTO_INCREMENT,
  `STT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MSSV` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `HoLot` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `Ten` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `NgaySinh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `MaLop` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `TenLop` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `DienThoai` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `Email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `QueQuan` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `GhiChu` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dt_expired` date NULL DEFAULT '9999-12-31',
  PRIMARY KEY (`SK_SV`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;



DROP TABLE IF EXISTS `lophoc`;
CREATE TABLE `lophoc`  (
  `SK_LH` int(11) NOT NULL AUTO_INCREMENT,
  `STT` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MaLH` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MaMH` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `NamHoc` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  PRIMARY KEY (`SK_LH`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


DROP TABLE IF EXISTS `dangki`;
CREATE TABLE `dangki`  (
  `SK_DK` int(11) NOT NULL AUTO_INCREMENT,
  `STT` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MaDK` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MaSV` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MaLH` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `TimeDK` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  PRIMARY KEY (`SK_DK`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
