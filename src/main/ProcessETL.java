package main;

import componen_2.ExtractFileToStaging;
import component_1.DownloadFile;
import component_3.DataWarehouseMain;

public class ProcessETL {
	public static void main(String[] args) {
//		String id = args[0];
		String id = "1";
		new DownloadFile(id).downloadFileProcess();
		System.out.println("download finished");
		try {
			new ExtractFileToStaging(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Process load to staging fail");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		new DataWarehouseMain(args[0]).addDataToWarehouse();
		System.out.println("transform done");
	}
}
