package component_3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.ConnectDB;
import common.JavaMail;


public class WarehouseMaster {
	
	// Khởi tạo các connection kết nối tới db_control, db_staging, db_warehouse
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private Connection CONNECTION_WAREHOUSE = null;
	
	// Set ID config
	private String idConfig;
	
	// Tạo ra list để lưu các giá trị cần thiết
	private Map<String, String> dataControl; 
	private List<String> dataStaging; 
	private List<String> dataWarehouse; 
		
	// Các thuộc tính fix cứng
	private final String PASSWORD = ""; 
	private final String TRANSFORM_SUCCESS = "TS"; 
	private final String TRANSFORM = "TF"; 
	
	/**
	 * 1. Connect tới db_control 
	 * 2. Lấy các thuộc tính cần thiết add vào list
	 * 	  dataControl
	 */
	public void connectDataControl() throws SQLException {
		// Tạo connect tới db control
		CONNECTION_CONTROL = ConnectDB.getConectionControl("root", PASSWORD);
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		
		// Tạo câu truy vấn
		String sql = "select databasse, user_des, pwd_des, table_name_des,"
				+ "column_number, column_name,dbwarehouse_name, dbwarehouse_user,"
				+ "dbwarehouse_password, dbwarehouse_table, date_dim_name "
				+ "from data_config where id =" + idConfig;
		
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

	}
	
	/**
	 * 1. Connect tới db_staging nhờ từ những thuộc tính có trong list data_Control
	 * 2. Lấy toán bộ record trong db_staging add vào list data_Staging 
	 * 3. Nếu field nào rỗng thì gán giá trị là "NULL"
	 */
	public void connectDataStaging() throws SQLException {
		
		// Tạo kết nối tới staging và query lấy toàn bộ record của nó
		CONNECTION_STAGING = ConnectDB.getConnection
							(dataControl.get("db_name_staging"),
							 dataControl.get("user_name_staging"),
							 dataControl.get("password_staging"));

		Statement statementStaging = CONNECTION_STAGING.createStatement();
		String sql = "select * from " + dataControl.get("table_name_staging");
		ResultSet rsStaging = statementStaging.executeQuery(sql);
		
		// Tạo list dataStaging
		dataStaging = new ArrayList<String>();
		
		/*
		 * Chạy từng phần tử trong resultset
		 * Định dạng record thành string "A|B|C". Mỗi field ngăn cách bởi dấu |
		 * Nếu field là NULL thì gán là "NULL"
		 */
		
		while (rsStaging.next()) {
			String temp = "";
			for (int i = 1; i < Integer.parseInt(dataControl.get("number_of_column")) + 1; i++) { // column_number
				if (rsStaging.getString(i).equalsIgnoreCase("\r") 
					|| rsStaging.getString(i).equalsIgnoreCase(" \r")
					|| rsStaging.getString(i).equalsIgnoreCase("")) {
					temp += "|" + "NULL";
					continue;
				}
				
				// Trim để loại bỏ khoảng trống 2 đầu, nếu dữ liệu lỗi
				temp += "|" + rsStaging.getString(i).trim(); 
			}
			// Bỏ dấu "|" ở ban đầu
			temp = temp.substring(1);
			
			// Add chuỗi A|B|C|D... vào list
			dataStaging.add(temp);

		}

	}
	
	/**
	 * 1. Kết nối tới database_warehouse
	 * 2. Lấy ra toàn bộ dữ liệu của warehouse add vào list data_warehouse
	 */
	public void connectDataWarehouse() throws SQLException {
		// Khởi tạo kết nối và truy vấn lấy ra dữ liệu
		CONNECTION_WAREHOUSE = ConnectDB.getConnect(
				dataControl.get("db_name_warehouse"),
				dataControl.get("user_name_warehouse"),
				dataControl.get("password_warehouse"));
		
		getDataWarehouse();

	}
	
	public void getDataWarehouse() throws SQLException {
		
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		ResultSet rsWarehouse = statementWarehouse.executeQuery("select * from " + dataControl.get("table_name_warehouse"));
		
		// Giống như đoạn connect với data staging
		dataWarehouse = new ArrayList<String>();

		while (rsWarehouse.next()) {
			String temp = "";
			
			for (int i = 2; i < Integer.parseInt(dataControl.get("number_of_column")) + 2; i++) {
				temp += "|" + rsWarehouse.getString(i);
			}
			temp = temp.substring(1);
			dataWarehouse.add(temp);
		}
		System.out.println(dataWarehouse);
	}

	
	private void handleData() throws SQLException {
		
		// Chạy lần lượt từng phần tử trong dataStaging
				for (String dataIndex : dataStaging) {
					// Thực hiện hàm checkData
					switch (checkData(dataIndex)) {
					// 1. Nếu trùng hoàn toàn thì nhảy sang record tiếp theo
					case 1:
						break;
					// 2. Nếu trùng MSSV thì update dt_expired và addData();
					case 2:
						addData(dataIndex);
						break;
					// 0. Nếu bình thường thì addData();
					default:
						addData(dataIndex);
						break;
					}
					
					// Cập nhật lại dataWarehouse
					getDataWarehouse();

				}
		
	}
	
	
	
	private int checkData(String dataIndex) throws SQLException {
		// 1. Giống hoàn toàn
		if (dataWarehouse.contains(dataIndex)) {
			return 1;
		}
		// 2. Giống trường khóa chính (MSSV)
		String arrDataIndex[] = dataIndex.split("[|]");
		String valueIndex = "";
		int index = 2;
		
		if (dataControl.get("table_name_warehouse").equalsIgnoreCase("sinhvien") ||
			dataControl.get("table_name_warehouse").equalsIgnoreCase("monhoc")) {	
			
			index = 1;	
		}
		
		valueIndex = arrDataIndex[index];

		int i = 0;
		for (String dtWareHouse : dataWarehouse) {
			i += 1;
			String arrWarehouse[] = dtWareHouse.split("[|]");
			
			
			if (valueIndex.equalsIgnoreCase(arrWarehouse[index])) {
				setExpireDate(Integer.toString(i));
				return 2;
			}

		}
		return 0;
	}
	
	// Tham số truyền vào là SK_ID, set dt_expired của SK_ID thành ngày hiện tại
	
	private void setExpireDate(String id) throws SQLException {
		
		String temp = "";
		
		switch (dataControl.get("table_name_warehouse")) {
		case "sinhvien":
			temp = " WHERE SK_SV =";
			break;
		case "monhoc":
			temp = " WHERE SK_MH =";
			break;
		case "lophoc":
			temp = " WHERE SK_LH =";
			break;
		case "dangki":
			temp = " WHERE SK_DK =";
			break;

		default:
			break;
		}
		
		String sql = "UPDATE " + dataControl.get("table_name_warehouse") +
					 " SET dt_expired = CURDATE()" + temp + id;
		
		if(dataControl.get("table_name_warehouse").equals("monhoc")) {
			
			sql = "UPDATE " + dataControl.get("table_name_warehouse") +
					 " SET date_expired = \"31-12-2013\", date_change = CURDATE()" + temp + id;
			
		}
		

		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		statementWarehouse.executeUpdate(sql);

	}

	
	private void addData(String dataIndex) throws SQLException {
		// value để thêm vào warehouse
		String arrValue[] = dataIndex.split("[|]");
		// những giá trị của column name
		String arrFieldColumn[] = dataControl.get("column_name").split(",");
		
		String value = "";
		String valueColumn = "";
		
	//	String dateDimValue = getIdDateDim(arrValue[4]);

		// Assign column name
		for (String string : arrFieldColumn) {
			valueColumn += ", " + string;
		}
		// Asign column value
		for (String string : arrValue) {
			string = "N" + "'" + string + "'";
			value += ", " + string;
		}
		
		
		// Bỏ dấu , ở đầu
		value = value.substring(1);
		valueColumn = valueColumn.substring(1);	
		
		String sql = "INSERT INTO " + dataControl.get("table_name_warehouse") + 
					 "(" + valueColumn + ") VALUES(" + value + ");";
		
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		System.out.println(sql);
		int rows = statementWarehouse.executeUpdate(sql);
		System.out.println(rows);
	}
	
	// Kiểm tra trong config_log có record nào status = TF hay không?
	private boolean checkStatus() throws SQLException {
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		String sql = "select count(*) from data_config_log where status = \"" +
					TRANSFORM + "\" and id = " + idConfig;
		
		System.out.println(sql);

		ResultSet rsControl = statementControl.executeQuery(sql);
		String result = "";
		while (rsControl.next()) {
				result = rsControl.getString(1);
				
		}
		
		System.out.println(result);
		if(result.equals("0")) return false;
		return true;
	}
	
	// Update lại status sau khi transform 
	private void editStatus() throws SQLException {
		
		String sql = "UPDATE " + "data_config_log" +
					 " SET status = \"TC\" where status = \"TF\" and id =" + idConfig;
		
		
		
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		
		statementControl.executeUpdate(sql);
		
	}
	
	private void truncateTable() throws SQLException {
		
		String sql = "TRUNCATE TABLE " + dataControl.get("table_name_staging");
		
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		
		statementControl.execute(sql);
		
	}
	
	public void addDataToWarehouse() {
		try {
			connectDataControl();
			if(checkStatus()) {
				connectDataStaging();
				connectDataWarehouse();
				handleData();
			// Khi nào chạy thiệt mới mở comment, chứ không nó xóa hết data test hết :v 
			//	editStatus();
			//	truncateTable();
			}
			else {
				JavaMail.send("thuongnguyen.it78@gmail.com", "Datawarehouse", "Nothing file to load");
				
			}
			
		} catch (SQLException e) {
			System.out.println(e);
			JavaMail.send("thuongnguyen.it78@gmail.com", "Datawarehouse", "Error when loading");
		}
		
	}
	
	public static void main(String[] args) {
		WarehouseMaster main = new WarehouseMaster();
		main.idConfig = "4";
		main.addDataToWarehouse();
		
	}

}
