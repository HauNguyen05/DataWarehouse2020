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
  `SK` int(255) NOT NULL AUTO_INCREMENT,
  `STT` int(255) NOT NULL,
  `ma_MH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ten_MH` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tin_chi` int(255) NULL DEFAULT NULL,
  `khoa_QL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `ghi_chu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `date_exprite` date NULL DEFAULT '9999-12-31',
  `date_change` date NULL DEFAULT '9999-12-31',
  PRIMARY KEY (`SK`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warehouse_student
-- ----------------------------
DROP TABLE IF EXISTS `warehouse_student`;
CREATE TABLE `warehouse_student`  (
  `SK_SV` int(11) NOT NULL AUTO_INCREMENT,
  `STT` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `MSSV` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `HoLot` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `Ten` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
  `NgaySinh` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'NULL',
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
