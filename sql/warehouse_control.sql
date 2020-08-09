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

 Date: 02/08/2020 19:19:47
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
  `delimeter` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbwarehouse_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbwarehouse_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbwarehouse_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbwarehouse_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `date_dim_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config
-- ----------------------------
INSERT INTO `data_config` VALUES (1, 'drive.ecepvn.org', '2227', 'guest_access', '123456', '/volume1/ECEP/song.nguyen/DW_2020/data/', 'D:/DataWarehouse2020/data/source/SinhVien', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'staging', 'root', '', 'sinhvien', '11', 'STT,MSSV,HoLot,Ten,NgaySinh,MaLop,TenLop,DienThoai,Email,QueQuan,GhiChu', 'log_config_SinhVien.txt', 'sinhvien_(sang|chieu)_nhom([0-9]|[0-9][0-9]).txt, sinhvien_(sang|chieu)_nhom([0-9]|[0-9][0-9]).csv, sinhvien_(sang|chieu)_nhom([0-9]|[0-9][0-9]).xlsx', '|', 'warehouse_main', 'root', '', 'sinhvien', 'date_dim');
INSERT INTO `data_config` VALUES (2, 'drive.ecepvn.org', '2227', 'guest_access', '123456', '/volume1/ECEP/song.nguyen/DW_2020/data/', 'D:/DataWarehouse2020/data/source/MonHoc', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'staging', 'root', '', 'monhoc', '6', 'STT,MaMH,TenMH,TinChi,KhoaQL,GhiChu', 'log_config_MonHoc.txt', '', '|', 'warehouse_main', 'root', '', 'monhoc', 'date_dim');
INSERT INTO `data_config` VALUES (3, 'drive.ecepvn.org', '2227', 'guest_access', '123456', '/volume1/ECEP/song.nguyen/DW_2020/data/', 'D:/DataWarehouse2020/data/source/LopHoc', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'staging', 'root', '', 'lophoc', '4', 'STT,MaLH,MaMH,NamHoc', ' log_config_LopHoc.txt', 'lophoc_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.txt, lophoc_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.csv, lophoc_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.xlsx', '|', 'warehouse_main', 'root', '', 'lophoc', 'date_dim');
INSERT INTO `data_config` VALUES (4, 'drive.ecepvn.org', '2227', 'guest_access', '123456', '/volume1/ECEP/song.nguyen/DW_2020/data/', 'D:/DataWarehouse2020/data/source/DangKy', 'jdbc:mysql://localhost:3306/', 'com.mysql.jdbc.Driver', 'staging', 'root', '', 'dangky', '5', 'STT,MaDK,MaSV,MaLH,TimeDK', 'log_config_DangKy.txt', 'dangky_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.txt, dangky_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.csv, dangky_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.xlsx', '|', 'warehouse_main', 'root', '', 'dangki', 'date_dim');

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
  PRIMARY KEY (`id`, `file_name`) USING BTREE,
  CONSTRAINT `data_config_log_ibfk_1` FOREIGN KEY (`id`) REFERENCES `data_config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_config_log
-- ----------------------------
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom11.xlsx', 'TF', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom13.xlsx', 'FAIL', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom14.xlsx', 'TF', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom15.xlsx', 'TF', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom16.xlsx', 'FAIL', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom2.xlsx', 'TF', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom3.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.txt', 'ER', 'txt', 0, 1);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom4.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom6.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_chieu_nhom7.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom1.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom11.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom13.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom14.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom15.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom16.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom2.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom4.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom6.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom7.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom8.txt', 'ER', 'txt', 0, 1);
INSERT INTO `data_config_log` VALUES (1, 'sinhvien_sang_nhom9.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (2, 'Monhoc2013.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (2, 'Monhoc2014.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_chieu_nhom15_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_chieu_nhom3_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_chieu_nhom4_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom11_2020.txt', 'ER', 'txt', 0, 1);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom12_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom15_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom4_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom5_2020.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom7_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (3, 'lophoc_sang_nhom8_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_chieu_nhom15_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_chieu_nhom3_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_chieu_nhom4_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom11_2020.txt', 'ER', 'txt', 0, 1);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom12_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom15_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom4_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom5_2020.csv', 'ER', 'csv', 0, 1);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom7_2020.xlsx', 'ER', 'xlsx', 0, 0);
INSERT INTO `data_config_log` VALUES (4, 'dangky_sang_nhom8_2020.xlsx', 'ER', 'xlsx', 0, 0);

SET FOREIGN_KEY_CHECKS = 1;
