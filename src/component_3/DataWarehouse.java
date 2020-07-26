package component_3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mysql.cj.xdevapi.Table;

import common.ConnectDB;

public class DataWarehouse {

	// Tạo các connection kết nối tới db_control, db_staging, db_warehouse
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private Connection CONNECTION_WAREHOUSE = null;

	// Tạo ra list để lưu các giá trị cần thiết
	List<String> dataControl;
	List<String> dataStaging;
	List<String> dataWarehouse;

	// Các thuộc tính fix cứng
	final String PASSWORD = "chkdsk"; // password my-sql
	final String TRANSFORM_SUCCESS = "TS"; // transform successful
	final String TRANSFORM_FAIL = "TF"; // transform

	/**
	 * 1. Connect tới db_control 2. Lấy các thuộc tính cần thiết add vào list
	 * dataControl
	 */
	public void connectDataControl() throws SQLException {
		CONNECTION_CONTROL = ConnectDB.getConectionControl("root", PASSWORD);
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		String sql = "select databasse, user_des, pwd_des, table_name_des,"
				+ "column_number, column_name,dbwarehouse_name, dbwarehouse_user,"
				+ "dbwarehouse_password, dbwarehouse_table from data_config limit 1;";

		ResultSet rsControl = statementControl.executeQuery(sql);

		dataControl = new ArrayList<String>();

		while (rsControl.next()) {
			dataControl.add(rsControl.getString(1)); // DatabaseName_Staging
			dataControl.add(rsControl.getString(2)); // Username_Staging
			dataControl.add(rsControl.getString(3)); // Password_Staging
			dataControl.add(rsControl.getString(4)); // TableName_Staging
			dataControl.add(rsControl.getString(5)); // Column_Number
			dataControl.add(rsControl.getString(6)); // Column_Name
			dataControl.add(rsControl.getString(7)); // DatabaseName_Warehouse
			dataControl.add(rsControl.getString(8)); // Username_Warehouse
			dataControl.add(rsControl.getString(9)); // Password_Warehouse
			dataControl.add(rsControl.getString(10)); // Table_Warehouse

		}
		System.out.println(dataControl);

	}

	/**
	 * 1. Connect tới db_staging nhờ từ những thuộc tính có trong list data_Control
	 * 2. Lấy toán bộ record trong db_staging add vào data_Staging 3. Nếu trường nào
	 * rỗng thì gán giá trị là "NULL"
	 */
	public void connectDataStaging() throws SQLException {

		CONNECTION_STAGING = ConnectDB.getConnection(dataControl.get(0), dataControl.get(1), dataControl.get(2));

		Statement statementStaging = CONNECTION_STAGING.createStatement();

		ResultSet rsStaging = statementStaging.executeQuery("select * from " + dataControl.get(3));

		dataStaging = new ArrayList<String>();

		while (rsStaging.next()) {
			String temp = "";
			for (int i = 1; i < Integer.parseInt(dataControl.get(4)) + 1; i++) { // column_number
				String a = rsStaging.getString(i);
				
				if (rsStaging.getString(i).equalsIgnoreCase("\r") 
					|| rsStaging.getString(i).equalsIgnoreCase("")) {
					temp += "," + "NULL";
					continue;
				}
				
				temp += "," + rsStaging.getString(i);
			}
			temp = temp.substring(1);
			dataStaging.add(temp);

		}
		System.out.println(dataStaging);

	}

	/**
	 * 1. Connect tới db_warehouse nhờ từ những thuộc tính có trong list
	 * data_Control 2. Lấy toàn bộ record trong db_warehouse add vào list
	 * data_Warehouse
	 */
	public void connectDataWarehouse() throws SQLException {

		CONNECTION_WAREHOUSE = ConnectDB.getConnection(dataControl.get(6), dataControl.get(7), dataControl.get(8));
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		ResultSet rsWarehouse = statementWarehouse.executeQuery("select * from " + dataControl.get(9));

		dataWarehouse = new ArrayList<String>();

		while (rsWarehouse.next()) {

			String temp = "";
			for (int i = 2; i < Integer.parseInt(dataControl.get(4)) + 2; i++) {
				temp += "," + rsWarehouse.getString(i);
			}
			temp = temp.substring(1);
			dataWarehouse.add(temp);

		}
		System.out.println(dataWarehouse);

	}

	public void handleData() throws SQLException {

		// Chạy lần lượt từng phần tử trong dataStaging
		for (String dataIndex : dataStaging) {

			switch (checkData(dataIndex)) {
			// 1. Nếu trùng hoàn toàn thì nhảy sang record tiếp theo
			case 1:
				break;
			// 2. Nếu nó trùng MSSV thì update lại SV trong dt_warehouse và addData();
			case 2:
				addData(dataIndex);
				break;
			// 0. Nếu bình thường thì addData();
			default:
				addData(dataIndex);
				break;
			}

		}

	}

	private void setExpireDate(String id) throws SQLException {
		String sql = "UPDATE " + dataControl.get(9) + " SET dt_expired = CURDATE()" + " WHERE SK_SV = " + id;

		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		int rsWarehouse = statementWarehouse.executeUpdate(sql);

	}

	private void executeSuccess() throws SQLException {
		// Update status file trong log thành TS
		updateStatus(TRANSFORM_SUCCESS);
		
		// Truncate Staging
		truncateStaging();
	}

	private void updateStatus(String status) throws SQLException {
		String sql = "UPDATE " + "data_config_log" +" SET status =" + status +"WHERE status = TF limit 1";
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		ResultSet rsControl = statementControl.executeQuery(sql);
	}

	private void truncateStaging() throws SQLException {
		String sql = "TRUNCATE TABLE " + dataControl.get(3);

		Statement statementStaging = CONNECTION_STAGING.createStatement();
		ResultSet rsStaging = statementStaging.executeQuery(sql);
	}
	
	private void checkStatus() {
		
	}

	private void addData(String dataIndex) throws SQLException {

		String arr[] = dataIndex.split(",");
		String value = "";

		for (String string : arr) {
			string = "N" + "'" + string + "'";
			value += ", " + string;
		}

		value = value.substring(1);
		// CHU Y
		String sql = "INSERT INTO " + dataControl.get(9)
				+ " (STT, MSSV, HoLot, Ten, NgaySinh, MaLop, TenLop,"
				+ " DienThoai, Email, QueQuan, GhiChu)" 
				+ " VALUES(" + value
				+ ");";
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		int rows = statementWarehouse.executeUpdate(sql);
		System.out.println(rows);

	}

	private int checkData(String dataIndex) throws SQLException {
		// 1. Giống hoàn toàn
		if (dataWarehouse.contains(dataIndex)) {
			return 1;
		}
		// 2. Giống trường khóa chính (MSSV)
		String arrDataIndex[] = dataIndex.split(",");
		
		int i = 0;
		for (String dtWareHouse : dataWarehouse) {
			i += 1;
			String arrWarehouse[] = dtWareHouse.split(",");
			if (arrDataIndex[1].equals(arrWarehouse[1])) {			
				setExpireDate(Integer.toString(i));
				return 2;
			}

		}
		return 0;
	}

	public void addDataToWarehouse() throws SQLException {

		connectDataControl();
		connectDataStaging();
		connectDataWarehouse();
		handleData();

	}

	public static void main(String[] args) throws SQLException {
		DataWarehouse dw = new DataWarehouse();
		dw.addDataToWarehouse();

//		dw.connectDataControl();
//		dw.connectDataStaging();
//		dw.connectDataWarehouse();

		/*
		 * Connection conn = ConnectDB.getConnection("warehouse_control", "root",
		 * "chkdsk"); if(conn != null) System.out.println("yes man"); else
		 * System.out.println("not ok");
		 * 
		 */

	}

}
