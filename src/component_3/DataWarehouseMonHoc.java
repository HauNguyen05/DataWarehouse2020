package component_3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import common.ConnectDB;
import common.JavaMail;

public class DataWarehouseMonHoc {
	private Connection connectStaging = null;
	private Connection connectWarehouse = null;
	private Connection connectControl;
	private String idConfig;
	private Map<String, String> information = new HashMap<String, String>();

	public DataWarehouseMonHoc(String id) {
		this.idConfig = id;
		try {
			connectControl = ConnectDB.getConectionControl("root", "");
			information = getInformation();
			getConnectToStaging();
			getConnectToWarehouse();
			transformMonHocToWarehouse();
			System.out.println("transform data mon hoc done");
		} catch (SQLException e) {
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse - Can not connect to database Control",
					"I can not connect to db");
		}
	}

	/*
	 * get connection to database Staging
	 */
	public Connection getConnectToStaging() {
		try {
			return connectStaging = ConnectDB.getConnection(information.get("destination"),
					information.get("server_des"), information.get("databasse"), information.get("user_des"),
					information.get("pwd_des"));
		} catch (SQLException e) {
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse - Can not connect to database Staging",
					"I can not connect to db");
			return null;
		}
	}

	/*
	 * get connection to database Staging
	 */
	public Connection getConnectToWarehouse() {
		try {
			return connectWarehouse = ConnectDB.getConnection(information.get("destination"),
					information.get("server_des"), information.get("db_warehouse_main"), information.get("db_warehouse_user"),
					information.get("pwd_des"));
		} catch (SQLException e) {
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse - Can not connect to database Warehouse",
					"I can not connect to db");
			return null;
		}
	}

	/*
	 * get infor to implement transform data
	 */
	public Map<String, String> getInformation() {
		Map<String, String> result = new HashMap<String, String>();
		String sql = "SELECT  destination, server_des, databasse,user_des,pwd_des,table_name_des, path_dir_src,column_number, column_name ,file_logs, dbwarehouse_name, dbwarehouse_user, dbwarehouse_password from data_config WHERE data_config.id = '"
				+ this.idConfig + "'";
		try {
			ResultSet rs = connectControl.createStatement().executeQuery(sql);
			while (rs.next()) {
				result.put("destination", rs.getString(1));
				result.put("server_des", rs.getString(2));
				result.put("databasse", rs.getString(3));
				result.put("user_des", rs.getString(4));
				result.put("pwd_des", rs.getString(5));
				result.put("table_name_des", rs.getString(6));
				result.put("path_dir_src", rs.getString(7));
				result.put("column_number", rs.getString(8));
				result.put("column_name", rs.getString(9));
				result.put("file_logs", rs.getString(10));
				result.put("db_warehouse_main", rs.getString(11));
				result.put("db_warehouse_user", rs.getString(12));
				result.put("db_warehouse_password", rs.getString(13));
				
			}
		} catch (SQLException e) {
			System.out.println("excute query fail");
		}
		return result;
	}

	public void transformMonHocToWarehouse() {
		int numCol = Integer.parseInt(information.get("column_number"));
		String sql = "select `";
		for (int i = 1; i <= numCol; i++) {
			sql += i + "`, `";
		}
		sql = sql.substring(0, sql.length() - 3);
		sql += " from monhoc ";
		try {
			ResultSet rs = connectStaging.createStatement().executeQuery(sql);
			while (rs.next()) {
				List<String> monhoc = new ArrayList<String>();
				monhoc.add(rs.getString(1));
				monhoc.add(rs.getString(2));
				monhoc.add(rs.getString(3));
				monhoc.add(rs.getString(4));
				monhoc.add(rs.getString(5));
				monhoc.add(rs.getString(6));
				handelData(monhoc);
				String truncate = "TRUNCATE TABLE monhoc";
				connectStaging.createStatement().execute(truncate);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void handelData(List<String> monhoc) {
		String sql = "select STT from monhoc where STT='" + monhoc.get(0) + "'";
		boolean update = false;
		boolean insertMH = true;
		try {
			ResultSet rs = connectWarehouse.createStatement().executeQuery(sql);
			while (rs.next()) {
				String STT = changeData(monhoc.get(0));
				String maMH = changeData(monhoc.get(1));
				String tenMH = changeData(monhoc.get(2));
				int tc = changeDataInt(monhoc.get(3));
				String khoaQL = changeData(monhoc.get(4));
				String ghichu = changeData(monhoc.get(5));
				update = true;
				String sql1 = "select STT from monhoc where STT='" + STT + "' and ma_MH ='" + maMH + "' and ten_MH='"
						+ tenMH + "' and tin_chi=" + tc + " and khoa_QL='" + khoaQL + "' and ghi_chu='" + ghichu + "'";
				rs = connectWarehouse.createStatement().executeQuery(sql1);
				while (rs.next()) {
					update = false;
					insertMH = false;
				}
			}
//			System.out.println("update: "+ update +"\tinsert: "+insertMH);
			if (update) {
				String dateChange = currentDate();
				String sqlUpdate = "update `monhoc` set date_exprite='2013-12-31', date_change='" + dateChange
						+ "' where STT ='" + monhoc.get(0) + "' and date_exprite='9999-12-31';";
				PreparedStatement statement = connectWarehouse.prepareStatement(sqlUpdate);
				statement.executeUpdate();
			}
			if (insertMH) {
				insertMonHoc(monhoc);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void insertMonHoc(List<String> monhoc) {
		String insert = "insert into monhoc(STT, ma_MH, ten_MH, tin_chi, khoa_QL, ghi_chu, date_exprite, date_change) VALUES(?,?,?,?,?,?,'9999-12-31', '9999-12-31')";
		try {
			PreparedStatement statement = connectWarehouse.prepareStatement(insert);
			statement = connectWarehouse.prepareStatement(insert);
			statement.setString(1, changeData(monhoc.get(0)));
			statement.setString(2, changeData(monhoc.get(1)));
			statement.setString(3, changeData(monhoc.get(2)));
			statement.setInt(4, changeDataInt((monhoc.get(3))));
			statement.setString(5, changeData(monhoc.get(4)));
			statement.setString(6, changeData(monhoc.get(5)));
			statement.executeUpdate();
		} catch (Exception e) {
			System.out.println("can not insert monhoc");
		}
	}

	public String changeData(String value) {
		if (value.trim().equals("") || value == null) {
			return value = "null";
		} else
			return value;
	}

	public int changeDataInt(String value) {
		if (value.trim().equals("") || value == null) {
			return -1;
		} else {
			return Integer.valueOf(value);
		}
	}

	public String currentDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		return format.format(cal.getTime());
	}
public static void main(String[] args) {
	new DataWarehouseMonHoc("2");
}
}
