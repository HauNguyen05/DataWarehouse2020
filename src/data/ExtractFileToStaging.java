package data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtractFileToStaging {
	Connection connectStaging;

	public ExtractFileToStaging() throws SQLException {
		// TODO Auto-generated constructor stub
		this.connectStaging = ConnectDB.getConectionStaging();
	}

	// Load data File CSV to Staging
	static void loadToStaging(String delimetter, String source, int ignore) {
		try {
			Connection connection = ConnectDB.getConectionStaging();
			String loadQuery = "LOAD DATA INFILE '" + source + "' INTO TABLE `sinhvien` FIELDS TERMINATED BY '\\"
					+ delimetter + "' LINES TERMINATED BY '\n' IGNORE " + ignore+" LINES";
			System.out.println(loadQuery);
			PreparedStatement stmt = connection.prepareStatement(loadQuery);
			stmt.execute(loadQuery);
			stmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void changeStatusFile(String fileName,String status) throws SQLException {
		// TODO Auto-generated method stub
		Connection connection = ConnectDB.getConectionControl();
		String update = "UPDATE `data_config_log` SET status ='TF' WHERE file_name='"+ fileName+"'";
		PreparedStatement statement = connection.prepareStatement(update);
		statement.executeUpdate();
		System.out.println("thay đổi status  thành công");
	}

	public static void main(String[] args) throws SQLException, IOException {
		new ExtractFileToStaging().insetDataAllFile();
	}

	public void insetDataAllFile() throws SQLException, IOException {
		Connection connection = ConnectDB.getConectionControl();
		String sql = "select source,delimeter,file_name, ignore_record, unzip, file_type from data_config inner join data_config_log"
				+ " on data_config_log.id = data_config.id where status = 'ER'";
		PreparedStatement statement1 = connection.prepareStatement(sql);
		ResultSet resultSet = statement1.executeQuery();
		while (resultSet.next()) {
			String source = resultSet.getString(1);
			String deli = resultSet.getString(2);
			String fileName = resultSet.getString(3);
			int ignore = Integer.parseInt(resultSet.getString(4));
			source =source.concat(fileName);
			// Load data from file csv to table staging in Database Staging
			loadToStaging(deli, source, ignore);
			changeStatusFile(fileName,"TF");
		}
	}

}
