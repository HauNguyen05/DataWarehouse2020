create database warehouse_main;

use warehouse_main;

create table warehouse_student (
STT int PRIMARY KEY AUTO_INCREMENT,
MSSV nvarchar(10) DEFAULT 'NULL',
HoLot nvarchar(50) DEFAULT 'NULL',
Ten nvarchar(20) DEFAULT 'NULL',
NgaySinh nvarchar(15) DEFAULT 'NULL',
MaLop nvarchar(10) DEFAULT 'NULL',
TenLop nvarchar(50) DEFAULT 'NULL',
DienThoai nvarchar(15) DEFAULT 'NULL',
Email nvarchar(50) DEFAULT 'NULL',
QueQuan nvarchar(50) DEFAULT 'NULL',
GhiChu nvarchar(50),
dt_expired date DEFAULT '9999-12-31'
);






