/*
 Navicat Premium Data Transfer

 Source Server         : sinhvien
 Source Server Type    : MySQL
 Source Server Version : 100413
 Source Host           : localhost:3306
 Source Schema         : warehouse_control

 Target Server Type    : MySQL
 Target Server Version : 100413
 File Encoding         : 65001

 Date: 18/07/2020 22:07:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_config
-- ----------------------------
DROP TABLE IF EXISTS `data_config`;
CREATE TABLE `data_config`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `server_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `port_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pwd_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `path_remote` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `path_dir_src` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `destination` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `server_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `databasse` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pwd_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `table_name_des` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `column_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `column_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `file_logs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `syntax_file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config
-- ----------------------------
INSERT INTO `data_config` VALUES (1, 'drive.ecepvn.org', '2227', 'guest_access', '123456', '/volume1/ECEP/song.nguyen/DW_2020/data/', 'E:\\\\Java\\\\spring_mvc\\\\DataWarehouse2020\\\\data\\\\source\\\\SinhVien', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'warehouse_extra_db', 'root', '0985153812', 'data', '11', 'STT,MSSV,Họ Lót ,Tên SV,Ngày sinh,Mã Lớp,Lớp,Số điện thoại,Email,Quê quán,Ghi Chú', 'log_config_SinhVien.txt', 'sinhvien_sang_nhom, sinhvien_chieu_nhom');
INSERT INTO `data_config` VALUES (4, NULL, NULL, NULL, NULL, NULL, 'E:\\\\Java\\\\spring_mvc\\\\DataWarehouse2020\\\\data\\\\source\\\\MonHoc', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'warehouse_extra_db', 'root', '0985153812', 'monhoc', '6', 'STT, Mã MH,Tên Môn Học,TC,Khoa/BM quản lý,Ghi chú', 'log_config_MonHoc.txt', NULL);

-- ----------------------------
-- Table structure for data_config_log
-- ----------------------------
DROP TABLE IF EXISTS `data_config_log`;
CREATE TABLE `data_config_log`  (
  `id` int(255) NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `file_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unzip` int(255) NULL DEFAULT NULL COMMENT '0: là không, 1 là có',
  `ignore_record` int(255) NULL DEFAULT NULL COMMENT 'số hàng bỏ qua khi đọc file',
  `delimeter` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `file_name`) USING BTREE,
  CONSTRAINT `data_config_log_ibfk_1` FOREIGN KEY (`id`) REFERENCES `data_config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config_log
-- ----------------------------
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom11.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom14.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom15.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom16.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom2.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom3.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.csv', 'FAIL', 'csv', 0, 1, ',');
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.txt', 'FAIL', 'txt', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom5.txt', 'TF', 'txt', 0, 1, '|');
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom5.zip', 'FAIL', 'zip', 1, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom6.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom7.csv', 'TF', 'csv', 0, 1, ';');
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom06.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom1.txt', 'TF', 'txt', 0, 0, '	');
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom1.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom11.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom13.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom14.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom15.xlsx', 'FAIL', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom16.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom2.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom4.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom7.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom8.txt', 'FAIL', 'txt', 0, 1, '|');
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom9.xlsx', 'TF', 'xlsx', 0, 0, NULL);
INSERT INTO `data_config_log` VALUES (2, 'MonHoc2013.csv', 'TF', 'csv', 0, 1, '|');
INSERT INTO `data_config_log` VALUES (2, 'MonHoc2014.csv', 'TF', 'csv', 0, 1, '|');

--alter for datawarehouse. add extra field
alter table data_config
add dbwarehouse_name nvarchar(100), 
add dbwarehouse_user nvarchar(100), 
add dbwarehouse_password nvarchar(100), 
add dbwarehouse_table nvarchar(100);

SET FOREIGN_KEY_CHECKS = 1;
