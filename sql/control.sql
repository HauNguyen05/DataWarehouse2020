/*
 Navicat Premium Data Transfer

 Source Server         : sinhvien
 Source Server Type    : MySQL
 Source Server Version : 100408
 Source Host           : localhost:3306
 Source Schema         : control

 Target Server Type    : MySQL
 Target Server Version : 100408
 File Encoding         : 65001

 Date: 15/06/2020 16:47:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_config
-- ----------------------------
DROP TABLE IF EXISTS `data_config`;
CREATE TABLE `data_config`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `source` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `destination` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pwd_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pwd_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `table_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `column_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `port` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config
-- ----------------------------
INSERT INTO `data_config` VALUES (1, 'D:\\\\DataWarehouse\\\\data\\\\source\\\\', '', 'guest_access', 'root', '123456', NULL, 'sinhvien', 'STT,MSSV,Ho_Ten,Ngay_Sinh,Ma_Lop,Ten_Lop,SDT,Email,Que_Quan,Ghi_chu', '2227');

-- ----------------------------
-- Table structure for data_config_log
-- ----------------------------
DROP TABLE IF EXISTS `data_config_log`;
CREATE TABLE `data_config_log`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `file_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unzip` int(255) NULL DEFAULT NULL COMMENT '0: là không, 1 là có',
  `ignore_record` int(255) NULL DEFAULT NULL COMMENT 'số hàng bỏ qua khi đọc file',
  `delimeter` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`, `file_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config_log
-- ----------------------------
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom5.txt', 'ER', 'txt', 0, 1, '|');

SET FOREIGN_KEY_CHECKS = 1;
