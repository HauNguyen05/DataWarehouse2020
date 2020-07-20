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
	 * 1. Connect tới db_control 
	 * 2. Lấy các thuộc tính cần thiết add vào list
	 * dataControl
	 */
	public void connectDataControl() throws SQLException {
		CONNECTION_CONTROL = ConnectDB.getConectionControl("root", PASSWORD);
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		String sql = "select databasse, user_des, pwd_des, table_name_des, column_number, column_name,"
				+ "dbwarehouse_name, dbwarehouse_user, dbwarehouse_password, dbwarehouse_table from data_config limit 1;";
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
	 * 2. Lấy toán bộ record trong db_staging add vào data_Staging
	 */
	public void connectDataStaging() throws SQLException {

		CONNECTION_STAGING = ConnectDB.getConnection(dataControl.get(0), dataControl.get(1), dataControl.get(2));
		Statement statementStaging = CONNECTION_STAGING.createStatement();
		ResultSet rsStaging = statementStaging.executeQuery("select * from " + dataControl.get(3));
		dataStaging = new ArrayList<String>();

		while (rsStaging.next()) {
			String temp = "";
			for (int i = 2; i < Integer.parseInt(dataControl.get(4)) + 2; i++) { // column_number
				temp += "," + rsStaging.getString(i);
			}
			temp = temp.substring(1);
			dataStaging.add(temp);

		}
		System.out.println(dataStaging);

	}

	/**
	 * 1. Connect tới db_warehouse nhờ từ những thuộc tính có trong list
	 * data_Control 
	 * 2. Lấy toàn bộ record trong db_warehouse add vào list
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
		
		//Chạy lần lượt từng phần tử trong dataStaging
		for (String dataIndex : dataStaging) {
			
			switch (checkData(dataIndex)) {
			//1. Nếu trùng thì nhảy sang record tiếp theo
			case 1:
				break;
			//2. Nếu nó trùng các key update lại trường expired sau đó thì addData();
			case 2:
				updateTableWareHouse(dataIndex);
				addData(dataIndex);
				break;
			//3. Nếu có ít nhất một field bị rỗng thì edit lại thành "NULL" sau đó addData();
			case 3:
				editFieldWareHouse(dataIndex);
				addData(dataIndex);
				break;
			//4. Nếu bình thường thì addData();
			default:
				addData(dataIndex);
				break;
			}
					
			
		}
		
		
	}

	private void setExpireDate(String id) throws SQLException {
//		String sql = "UPDATE " + tableSDW + " SET date_expire = CURDATE(), id_file = " + currIDFile
//				+ "  WHERE id_student = " + id;
//		Statement st = connDW.createStatement();
//		st.execute(sql);

	}

//	private void executeSuccess() throws SQLException {
//		// update status file trong log thành TS
//		updateStatus(TRANSFORM_SUCCESS);
//		// truncate Staging
//		truncateStaging();
//	}

	private void truncateStaging() throws SQLException {
//		Statement state = connControll.createStatement();
//		// lấy thông tin data warehouse từ config
//		ResultSet rs = state.executeQuery("SELECT database_name FROM config_database WHERE config_database.stt = 1");
//		rs.next();
//		Statement stateS = connStaging.createStatement();
//		stateS.execute("TRUNCATE " + rs.getString(0) + "." + tableSDW);
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
		String sql = "INSERT INTO " + dataControl.get(10)
				+ " (MSSV, HoLot, Ten, NgaySinh, MaLop, TenLop, DienThoai, Email, QueQuan, GhiChu)" + " VALUES(" + value
				+ ");";
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		int rows = statementWarehouse.executeUpdate(sql);
		System.out.println(rows);

	}

	private void editFieldWareHouse(String dataIndex) {

	}

	private void updateTableWareHouse(String dataIndex) {

	}

	private int checkData(String dataIndex) {
		// 1. Giống hoàn toàn
		if (dataWarehouse.contains(dataIndex)) {
			return 1;
		}
		// 2. Giống trường khóa chính (MSSV hoặc số điện thoại hoặc email)
		String arrDataIndex[] = dataIndex.split(",");

		for (String dtWareHouse : dataWarehouse) {
			String arrWarehouse[] = dtWareHouse.split(",");

			if (arrDataIndex[0].equalsIgnoreCase(arrWarehouse[0]) || arrDataIndex[6].equalsIgnoreCase(arrWarehouse[6])
					|| arrDataIndex[7].equalsIgnoreCase(arrWarehouse[7])) {

				return 2;
			}

		}
		// 3. Nếu có field là NULL

		for (int i = 0; i < arrDataIndex.length; i++) {

			if (arrDataIndex[i].equals(""))
				return 3;

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
