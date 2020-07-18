/*
 Navicat Premium Data Transfer

 Source Server         : sinhvien
 Source Server Type    : MySQL
 Source Server Version : 100408
 Source Host           : localhost:3306
 Source Schema         : staging

 Target Server Type    : MySQL
 Target Server Version : 100408
 File Encoding         : 65001

 Date: 15/06/2020 16:42:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sinhvien
-- ----------------------------
-- DROP TABLE IF EXISTS `sinhvien`;
-- CREATE TABLE `sinhvien`  (
--   `STT` int(255) NOT NULL,
--   `Ho_lot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Ten` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `MSSV` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Ngay_sinh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Ma_lop` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Ten_lop` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `DT` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Que_quan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
--   `Ghi_chu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL
-- ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
use warehouse_extra_db;
create table data(
ID int primary key auto_increment,
`1` nvarchar(255) ,
`2` nvarchar(255),
`3` nvarchar(255),
`4` nvarchar(255),
`5` nvarchar(255),
`6` nvarchar(255),
`7` nvarchar(255),
`8` nvarchar(255),
`9` nvarchar(255),
`10` nvarchar(255),
`11` nvarchar(255)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 ;
-- ddd
use warehouse_extra_db;
create table monhoc(
ID int primary key auto_increment,
`1` nvarchar(255)  ,
`2` nvarchar(255),
`3` nvarchar(255),
`4` nvarchar(255),
`5` nvarchar(255),
`6` nvarchar(255)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



