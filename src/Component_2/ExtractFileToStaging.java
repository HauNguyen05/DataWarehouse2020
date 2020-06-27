package Component_2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Common.ConnectDB;

public class ExtractFileToStaging {
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private BufferedWriter BW = null;

// tao table data
	public void createTable(int column_number, String nameTable) {
		PreparedStatement pre = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("create table " + nameTable + "(");
			for (int i = 0; i < column_number; i++) {
				sql.append("`" + (i + 1) + "` nvarchar(255),");
				if (i == column_number - 1) {
					sql.append("`" + (i + 1) + "` nvarchar(255) )");
				}

			}
			// CONNECTION_STAGING.setAutoCommit(false);
			pre = CONNECTION_STAGING.prepareStatement(sql.toString());
			pre.executeUpdate();
			// CONNECTION_STAGING.commit();
		} catch (Exception e) {
			System.out.println("create table failure");
		}
	}

	public boolean addFileExcel(String path, String tableName, int column_number)
			throws ClassNotFoundException, SQLException, IOException {
		try {
			InputStream inputStream = new FileInputStream(new File(path));
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = sheet.iterator();
			// Traversing over each row of XLSX file
			rowIterator.next();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				List<String> data = new ArrayList<String>();
				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				int count = 1;
				for (int i = 0; i < column_number; i++) {
					Cell cell = row.getCell(i);
					if (cell == null) {
						data.add("");
					} else {
						data.add(cell.toString());
					}

				}
				// add data to database staging
				if (!addData(data, tableName, column_number)) {
					System.out.println("addFileExcel failure");
					BW.write("thêm data không thành công");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("addFileExcel failure " + e);
			BW.write("thêm data không thành công");
			return false;
		}

		return true;

	}

//	public void unzip(String source, String des) {
//		try {
//			Junrar.extract(source, des);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RarException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public boolean addData(List<String> data, String table_name_des, int column_number)
			throws ClassNotFoundException, SQLException, IOException {
		PreparedStatement statement = null;
		try {
			CONNECTION_STAGING.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			sql.append("insert into " + table_name_des);
			sql.append(" value(");
			for (int i = 0; i < column_number; i++) {
				if (i == data.size() - 1) {
					sql.append("?)");
					break;
				}
				sql.append("?,");
			}

			statement = CONNECTION_STAGING.prepareStatement(sql.toString());
			for (int i = 0; i < column_number; i++) {
				statement.setString(i + 1, data.get(i));
			}
			statement.executeUpdate();
			CONNECTION_STAGING.commit();
			return true;
		} catch (SQLException e1) {
			if (CONNECTION_STAGING != null) {
				try {
					CONNECTION_STAGING.rollback();
					System.out.println(" addData khong thanh cong");
					BW.write("Bug: JDBC không thể thêm data \r\n");
					BW.flush();
				} catch (SQLException e11) {
					e11.printStackTrace();
				}
			}
		}
		return false;
	}

	public void moveFileToError(String file) {
		File f = new File(file);
		String newPath = f.getParent() + File.separator + "Error" + File.separator + f.getName();
		try {
			Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
			BW.write("Move file " + f.getName() + " to folder error \r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Move file thanh bai");
		}
	}

	public void moveFileToSuccess(String file) {
		File f = new File(file);
		String newPath = f.getParent() + File.separator + "Successfully" + File.separator + f.getName(); // path folder
		try {
			Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
			BW.write("Move file " + f.getName() + "  to folder successfully \r\n");
		} catch (IOException e) {
			System.out.println("Move file thanh bai");
		}
	}

	// Load data File CSV to Staging
	public void loadToStaging(String path_dir_src, String file_name, String delimetter, int ignore_record,
			String file_type, String table_name_des, int column_number, boolean unzip)
			throws ClassNotFoundException, SQLException, IOException {
		String path = path_dir_src + "//" + file_name; // path File
		File file = new File(path);
		boolean check = false;
		if (!file.exists()) {
			System.out.println("file not found");
			BW.write("Bug: file không tồn tại \r\n");
			return;
		} else {
			try {
				if (file_type.equals("xlsx")) {
					check = addFileExcel(path, table_name_des, column_number);
				} else {

					String loadQuery = "LOAD DATA INFILE '" + path + "' INTO TABLE data FIELDS TERMINATED BY '\\"
							+ delimetter + "' LINES TERMINATED BY '\n' IGNORE " + ignore_record + " LINES";
					System.out.println(loadQuery);
					PreparedStatement state = CONNECTION_STAGING.prepareStatement(loadQuery);
					state.executeUpdate();
					state.close();
					check = true;
				}
				if (check) {
					BW.write("thêm data thành công \r\n");
					BW.flush();
					// chuyen doi trang thai file
					changeStatusFile(file_name, "TF");
					// chuyen file den thu muc thanh cong
					moveFileToSuccess(path);
				} else {
					// moveFileToError(path);
				}
			} catch (Exception e) {
				System.out.println(" loadToStaging that bai ");
				BW.write("thêm data thất bại \r\n");
				BW.flush();
				check = false;
				// chuyen file den thu muc error
				moveFileToError(path);
			
			}
		}
	}

	public void changeStatusFile(String fileName, String status) throws SQLException, IOException {
		// TODO Auto-generated method stub
		String update = "UPDATE `data_config_log` SET status =? WHERE file_name=?";
		PreparedStatement statement = CONNECTION_CONTROL.prepareStatement(update);
		statement.setString(1, status);
		statement.setString(2, fileName);
		statement.executeUpdate();
		BW.write("Thay doi status  thanh '" + status + "' \r\n");
	}

	public void insetDataAllFile() throws SQLException, IOException, ClassNotFoundException {
		String destination, server_des, databasse, user_des, pwd_des, table_name_des, delimiter, file_type,
				path_dir_src, file_name, file_logs = null;
		int column_number = 0;
		boolean unzip = false;
		int ignore_record;
		if (CONNECTION_CONTROL == null) {
			CONNECTION_CONTROL = ConnectDB.getConectionControl("root","0985153812");
		}
		String sql = "SELECT  destination,server_des, databasse,user_des,pwd_des,table_name_des, unzip, ignore_record,delimeter,file_type,path_dir_src,file_name,column_number ,file_logs from data_config inner join data_config_log"
				+ " on data_config_log.id = data_config.id where status = 'ER'";
		PreparedStatement statement1 = CONNECTION_CONTROL.prepareStatement(sql);
		ResultSet r = statement1.executeQuery();
		while (r.next()) {
			destination = r.getString(1);
			server_des = r.getString(2);
			databasse = r.getString(3);
			user_des = r.getString(4);
			pwd_des = r.getString(5);
			table_name_des = r.getString(6);
			unzip = r.getBoolean(7);
			ignore_record = Integer.valueOf(r.getString(8));
			delimiter = r.getString(9);
			file_type = r.getString(10);
			path_dir_src = r.getString(11);
			file_name = r.getString(12);
			column_number = Integer.valueOf(r.getString(13));
			file_logs = r.getString(14);
			if (CONNECTION_STAGING == null) {
				CONNECTION_STAGING = ConnectDB.getConnection(destination, server_des, databasse, user_des, pwd_des);
			}
			BW = new BufferedWriter(new FileWriter(new File(file_logs), true));
			BW.write("\r\n");
			BW.write("file: " + file_name + " " +new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()) + "\r\n");
			BW.flush();
			try {
			loadToStaging(path_dir_src, file_name, delimiter, ignore_record, file_type, table_name_des, column_number,
					unzip);
			}catch(Exception e) {
				System.out.println(e);
				BW.write("Bug:"+e+" \r\n");
				BW.flush();
				continue;
			}
			System.out.println("---------------------------------");
			BW.write("--------------------------------- \r\n");
			BW.flush();
			continue;
		}
		BW.close();
	}

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		ExtractFileToStaging a = new ExtractFileToStaging();
		a.insetDataAllFile();
		// a.moveFileToError("E:\\warehouse2020\\sinhvien_chieu_nhom1.txt");
		// a.unzip("E://warehouse2020//sinhvien_chieu_nhom5.rar", "E://warehouse2020");
//
	}

}