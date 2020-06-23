package Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	// private static Connection connectionControl = null;
	// private static Connection connectionStaging = null;

	public static Connection getConectionControl() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/warehouse_control?useSSL=false";
			return DriverManager.getConnection(url, "root", "0985153812");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Conneciton connectionConfig failure!");
			return null;
		}

	}

	public static Connection getConnection(String urlBasic, String driver, String nameDatabase, String userName,
			String password) throws SQLException {
		try {
			String url = urlBasic + nameDatabase;
			Class.forName(driver);
			return DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Connection database failure");
			return null;
		}

	}

	public static void main(String[] args) throws SQLException {
	
	}
}
