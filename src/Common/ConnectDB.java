package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static Connection connectionControl = null;
	private static Connection connectionStaging = null;

	public static Connection getConectionControl() throws SQLException {
		if (connectionControl == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connectionControl = (Connection) DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/control?useUnicode=true&characterEncoding=utf-8", "root", "");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("not");
			}
			return connectionControl;
		} else {
			return connectionControl;
		}

	}

	public static Connection getConectionStaging() throws SQLException {
		if (connectionStaging == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connectionStaging = (Connection) DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/staging?useUnicode=true&characterEncoding=utf-8", "root", "");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println("not");
			}
			return connectionStaging;
		} else {
			return connectionStaging;
		}

	}

	public static void main(String[] args) throws SQLException {
		if (getConectionControl() != null) {
			System.out.println("ok");
		} else {
			System.out.println("not");
		}
	}
}
