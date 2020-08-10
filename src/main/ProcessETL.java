package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.ConnectDB;
import componen_2.ExtractFileToStaging;
import component_1.DownloadFileSftp;
import component_3.WarehouseMaster;

public class ProcessETL {
	public static void main(String[] args) throws SQLException {
		Connection connect = ConnectDB.getConectionControl("root", "");
		// đếm số record trong data_config_log
		String sql1 = "select count(*) from data_config_log";
		ResultSet rs = connect.createStatement().executeQuery(sql1);
		int id = 0;
		// lấy kết quả trả về gán vào id
		if (rs.next()) {
			id = rs.getInt(1);
		}
		// nếu số dòng là 0 thì id =1
		if (id == 0) {
			id = 1;
		} else {// ngược lại thì chọn id từ database ra
			String sql = "SELECT  data_config.id from data_config inner join data_config_log on data_config_log.id = data_config.id where status = 'TS' or `status`='ER' limit 1";
			rs = connect.createStatement().executeQuery(sql);
			if (!rs.next()) {
				System.out.println("out of data to run");
				System.exit(0);
			} else {
				id = rs.getInt(1);
			}
		}
		System.out.println("run id: "+id);
		new DownloadFileSftp(id).downloadFileProcess();
		System.out.println("download finished\n");
		try {
			new ExtractFileToStaging(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Process load to staging fail");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		new WarehouseMaster(id).addDataToWarehouse();
		System.out.println("\ntransform done");
	}
}