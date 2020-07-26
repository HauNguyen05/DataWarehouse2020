package main;

import java.sql.SQLException;

import componen_2.ExtractFileToStaging;
import component_1.DownloadFile;
import component_3.DataWarehouse;
import component_3.DataWarehouseMonHoc;

public class ProcessETL {
	public static void main(String[] args) {
//		String id = args[0];
		String id ="1";
		new DownloadFile(id);
		System.out.println("download finished");
		try {
			new ExtractFileToStaging().insetDataAllFile(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Process load to staging fail");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		if (id == "2") {
			new DataWarehouseMonHoc(id);
			System.out.println("transform done");
		} else {
			try {
				new DataWarehouse(args[0]).addDataToWarehouse();
				System.out.println("transform done");
			} catch (SQLException e) {
				System.out.println("Process transform fail");
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}
	}
}
