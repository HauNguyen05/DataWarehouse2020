package component_3;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.ConnectDB;

public class DataWarehouse {
	
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private Connection CONNECTION_WAREHOUSE = null;
	
	List<String> dataControl;
	List<String> dataStaging;
	List<String> dataWarehouse;
	List<String> snapShot;
	
	String tableNameWareHouse = " warehouse_student ";
	
	public void connectDataControl() throws SQLException {
		CONNECTION_CONTROL = ConnectDB.getConnect("warehouse_control", "root", "chkdsk");
		Statement statementControl = CONNECTION_CONTROL.createStatement();
		String sql = "select databasse, user_des, pwd_des, table_name_des, column_number, column_name,"
				   + " dbwarehouse_name, dbwarehouse_user, dbwarehouse_password from data_config";
		ResultSet rsControl = statementControl.executeQuery(sql);
		
		dataControl = new ArrayList<String>();

		while (rsControl.next()) {	
			dataControl.add(rsControl.getString(1)); //DatabaseName_Staging
			dataControl.add(rsControl.getString(2)); //Username_Staging
			dataControl.add(rsControl.getString(3)); //Password_Staging
			dataControl.add(rsControl.getString(4)); //TableName_Staging
			dataControl.add(rsControl.getString(5)); //Column_Number
			dataControl.add(rsControl.getString(6)); //Column_Name
			dataControl.add(rsControl.getString(7)); //DatabaseName_Warehouse
			dataControl.add(rsControl.getString(8)); //Username_Warehouse
			dataControl.add(rsControl.getString(9)); //Password_Warehouse

		}
		
	}
	
	
	public void connectDataStaging() throws SQLException {
		
		CONNECTION_STAGING = ConnectDB.getConnect(dataControl.get(0), dataControl.get(1), dataControl.get(2));
		Statement statementStaging = CONNECTION_STAGING.createStatement();
		ResultSet rsStaging = statementStaging.executeQuery("select * from " + dataControl.get(3));
		
		dataStaging = new ArrayList<String>();

		while (rsStaging.next()) {
			
			String temp = "";
			for (int i = 2; i < Integer.parseInt(dataControl.get(4)) + 1 ; i++) {
				temp += "," + rsStaging.getString(i);
			}
			temp = temp.substring(1);
			dataStaging.add(temp);
			
		}
		System.out.println(dataStaging.toString());
		
	}
	
	public void connectDataWarehouse() throws SQLException {
		
		CONNECTION_WAREHOUSE = ConnectDB.getConnect(dataControl.get(6), dataControl.get(7), dataControl.get(8));
		Statement statementWarehouse = CONNECTION_WAREHOUSE.createStatement();
		ResultSet rsWarehouse = statementWarehouse.executeQuery("select * from " + tableNameWareHouse);
		
		dataWarehouse = new ArrayList<String>();
		
		while (rsWarehouse.next()) {
			
			String temp = "";
			for (int i = 2; i < Integer.parseInt(dataControl.get(4)) + 1; i++) {
				temp += "," + rsWarehouse.getString(i);
			}
			temp = temp.substring(1);
			dataWarehouse.add(temp);
			
		}
		System.out.println("warehouse");
		
	}


	
	public void handleData() throws SQLException {
		
		for (String dataIndex : dataStaging) {
			
			// Nếu trùng thì continue sang record tiếp theo
			if(checkData(dataIndex) == 1) {
				continue;
				
			}
			// Nếu nó trùng các giá trị MSSV, SDT, hoặc Email thì update lại trường expired sau đó thì addData();
			if(checkData(dataIndex) == 2) {
				updateTableWareHouse(dataIndex);
				addData(dataIndex);
				continue;
				
			}
			//Nếu có ít nhất một field bị rỗng thì edit lại thành "NULL" sau đó addData();
			if(checkData(dataIndex) == 3) {
				editFieldWareHouse(dataIndex);
				addData(dataIndex);
				continue;
				
			}
			//Nếu bình thường thì addData();
			if(checkData(dataIndex) == 0) {
				addData(dataIndex);
				continue;
				
			}
			
			
			
			
		}
		
	}
	
	

	private void addData(String dataIndex) throws SQLException {
		
		String arr[] = dataIndex.split(",");
		String value = "";
		
		for (String string : arr) {
			string = "N" + "'"+string +"'";
			value += ", " + string;
		}
		
		value = value.substring(1);
		//CHU Y
		String sql = "INSERT INTO " + tableNameWareHouse+"(MSSV, HoLot, Ten, NgaySinh, MaLop, TenLop, DienThoai, Email, QueQuan, GhiChu)"+ " VALUES(" + value + ");";	
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
		if(dataWarehouse.contains(dataIndex)) {
			return 1;
		}
		//2. Giống trường khóa chính (MSSV hoặc số điện thoại hoặc email)
		String arrDataIndex[] = dataIndex.split(",");
		
		for (String dtWareHouse : dataWarehouse) {
			String arrWarehouse[] = dtWareHouse.split(",");
			
			if(arrDataIndex[0].equalsIgnoreCase(arrWarehouse[0]) || 
			   arrDataIndex[6].equalsIgnoreCase(arrWarehouse[6]) ||
			   arrDataIndex[7].equalsIgnoreCase(arrWarehouse[7])) {
				
				return 2;
			}
			
		}
		//3. Nếu có field là NULL
		
		for (int i = 0; i < arrDataIndex.length; i++) {
			
			if(arrDataIndex[i].equals("")) return 3;
			
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

	}

}
