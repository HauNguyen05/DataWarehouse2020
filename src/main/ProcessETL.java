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
		int id = 1;
		while (id <= 4) {
			String sql = "SELECT  destination,server_des from data_config inner join data_config_log on data_config_log.id = data_config.id where data_config_log.id= "
					+ id + " and status = 'TS' or `status`='ER' limit 1";
			Connection connect = ConnectDB.getConectionControl("root", "");
			System.out.println(sql);
			ResultSet rs = connect.createStatement().executeQuery(sql);
			if (!rs.next()) {
				id++;
			}

			new DownloadFileSftp(id).downloadFileProcess();
			System.out.println("download finished");
			try {
				new ExtractFileToStaging(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Process load to staging fail");
				System.out.println(e.getMessage());
				System.exit(0);
			}
			new WarehouseMaster(id).addDataToWarehouse();
			System.out.println("transform done");

		}
	}
}