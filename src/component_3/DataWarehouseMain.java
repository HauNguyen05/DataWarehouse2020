package component_3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.ConnectDB;


public class DataWarehouseMain {
	// Tạo các connection kết nối tới db_control, db_staging, db_warehouse
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private Connection CONNECTION_WAREHOUSE = null;
	
	//Set ID config
	private String idConfig;
	
	// Tạo ra list để lưu các giá trị cần thiết
	Map<String, String> dataControl;
	List<String> dataStaging;
	List<String> dataWarehouse;
	Map<String, String> dateDim;
	
	
	// Các thuộc tính fix cứng
	final String PASSWORD = "chkdsk"; // password my-sql
	final String TRANSFORM_SUCCESS = "TS"; // transform successful
	final String TRANSFORM_FAIL = "TF"; // transform
	
	/**
	 * 1. Connect tới db_control 
	 * 2. Lấy các thuộc tính cần thiết add vào list
	 * 	  dataControl
	 */
	public void connectDataControl() throws SQLException {
		CONNECTION_CONTROL = ConnectDB.getConectionControl("root", PASSWORD);
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		String sql = "select databasse, user_des, pwd_des, table_name_des,"
				+ "column_number, column_name,dbwarehouse_name, dbwarehouse_user,"
				+ "dbwarehouse_password, dbwarehouse_table, date_dim_name from data_config where id =" + idConfig;

		ResultSet rsControl = statementControl.executeQuery(sql);

		dataControl = new HashMap<String, String>();

		while (rsControl.next()) {
			//Alt + Shirt + R
			dataControl.put("db_name_staging", rsControl.getString(1));
			dataControl.put("user_name_staging", rsControl.getString(2));
			dataControl.put("password_staging", rsControl.getString(3));
			dataControl.put("table_name_staging", rsControl.getString(4));
			dataControl.put("number_of_column", rsControl.getString(5));
			dataControl.put("column_name", rsControl.getString(6));
			dataControl.put("db_name_warehouse", rsControl.getString(7));
			dataControl.put("user_name_warehouse", rsControl.getString(8));
			dataControl.put("password_warehouse", rsControl.getString(9));
			dataControl.put("table_name_warehouse", rsControl.getString(10));
			dataControl.put("date_dim_name", rsControl.getString(11));
			

		}
		System.out.println(dataControl);

	}
	
	/**
	 * 1. Connect tới db_staging nhờ từ những thuộc tính có trong list data_Control
	 * 2. Lấy toán bộ record trong db_staging add vào data_Staging 
	 * 3. Nếu trường nào rỗng thì gán giá trị là "NULL"
	 */
	public void connectDataStaging() throws SQLException {

		CONNECTION_STAGING = ConnectDB.getConnection
							(dataControl.get("db_name_staging"),
							 dataControl.get("user_name_staging"),
							 dataControl.get("password_staging"));

		Statement statementStaging = CONNECTION_STAGING.createStatement();

		ResultSet rsStaging = statementStaging.
				  executeQuery("select * from " + dataControl.get("table_name_staging"));

		dataStaging = new ArrayList<String>();

		while (rsStaging.next()) {
			String temp = "";
			for (int i = 1; i < Integer.parseInt(dataControl.get("number_of_column")) + 1; i++) { // column_number
				
				if (rsStaging.getString(i).equalsIgnoreCase("\r") 
					|| rsStaging.getString(i).equalsIgnoreCase("")) {
					temp += "," + "NULL";
					continue;
				}
				
				temp += "," + rsStaging.getString(i).trim();
			}
			temp = temp.substring(1);
			dataStaging.add(temp);

		}
		System.out.println(dataStaging);

	}
	
	public void connectDataWarehouse() throws SQLException {
		
		CONNECTION_WAREHOUSE = ConnectDB.getConnection(
				dataControl.get("db_name_warehouse"),
				dataControl.get("user_name_warehouse"),
				dataControl.get("password_warehouse"));
		
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		ResultSet rsWarehouse = statementWarehouse.executeQuery("select * from " + dataControl.get("table_name_warehouse"));

		dataWarehouse = new ArrayList<String>();

		while (rsWarehouse.next()) {
			String temp = "";
			
			for (int i = 2; i < Integer.parseInt(dataControl.get("number_of_column")) + 2; i++) {
				temp += "," + rsWarehouse.getString(i);
			}
			
			temp = temp.substring(1);
			dataWarehouse.add(temp);

		}
		System.out.println(dataWarehouse);
		
		
		ResultSet rsDateDim = statementWarehouse.executeQuery("select `1`, `2` from " + dataControl.get("date_dim_name"));
		dateDim = new HashMap<String, String>();
		
		while (rsDateDim.next()) {
			dateDim.put(rsDateDim.getString(1), rsDateDim.getString(2));
			
		}
		System.out.println(dateDim);
		

	}
	
	private int checkHandlingData() {
		
		switch (dataControl.get("table_name_staging")) {
		case "sinhvien":
			return 1;
			
		case "monhoc":
			return 2;
			
		case "lophoc":
			return 3;
			
		case "dangki":
			return 4;

		default:
			return 0;
		}
	}
	
	private void directHandlingData() throws SQLException {
		
		switch (checkHandlingData()) {
		// sinhvien
		case 1:
			handleDataSinhVien();
			break;
			
		// monhoc
		case 2:
			handleDataMonHoc();
			break;
		// lophoc
		case 3:
			handleDataLopHoc();
			break;
		// dangki
		case 4:
			handleDataDangKi();
			break;

		default:
			break;
		}
	}
	
	private void handleDataSinhVien() throws SQLException {
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

	private void setExpireDate(String id) throws SQLException {
		String sql = "UPDATE " + dataControl.get("table_name_warehouse") +
					 " SET dt_expired = CURDATE()" + " WHERE SK_SV = " + id;

		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		int rsWarehouse = statementWarehouse.executeUpdate(sql);
		System.out.println(rsWarehouse);

	}
	private String getIdDateDim(String date) {
		if(!date.equals("NULL")) {
			for (Map.Entry me : dateDim.entrySet()) {
		          if(date.equalsIgnoreCase((String) me.getValue()))
		        	  return (String) me.getKey();
		        }
			
		} 
		return "";
	}
	
	private void addData(String dataIndex) throws SQLException {

		String arr[] = dataIndex.split(",");
		String arrFieldColumn[] = dataControl.get("column_name").split(",");
		String value = "";
		String valueColumn = "";
		String dateDim = getIdDateDim(arr[4]);

		
		for (String string : arrFieldColumn) {
			valueColumn += ", " + string;
		}

		for (String string : arr) {
			string = "N" + "'" + string + "'";
			value += ", " + string;
		}
		
		
		
		value = value.substring(1);
		valueColumn = valueColumn.substring(1);
		
		if(!dateDim.equals("")) {
			value += "," + dateDim;
			valueColumn += " " + dataControl.get("date_dim_name");
		}
		
		// CHU Y
		String sql = "INSERT INTO " + dataControl.get("table_name_warehouse") + 
					 "(" + valueColumn + ") VALUES(" + value + ");";
		
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		int rows = statementWarehouse.executeUpdate(sql);
	}

	private void handleDataMonHoc() {
		
	}
	
	private void handleDataLopHoc() {
		
	}
	
	private void handleDataDangKi() {
		
	}
	
	public void addDataToWarehouse() {
		try {
			connectDataControl();
			connectDataStaging();
			connectDataWarehouse();
			directHandlingData();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		DataWarehouseMain main = new DataWarehouseMain();
		main.idConfig = "1";
		main.addDataToWarehouse();
	}

}
