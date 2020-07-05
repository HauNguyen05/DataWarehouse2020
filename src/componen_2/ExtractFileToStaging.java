package componen_2;

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
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.ConnectDB;
import common.JavaMail;

public class ExtractFileToStaging {
	private Connection CONNECTION_CONTROL = null;
	private Connection CONNECTION_STAGING = null;
	private String EMAIL = "thongmap0909310872@gmail.com";
	private String SUBJECT = "Load file to staging";
	private BufferedWriter BW = null;

// tao table data
	public void createTable(int column_number, String nameTable) throws Exception {
		PreparedStatement pre = null;
		// Tao cau query
		StringBuilder sql = new StringBuilder();
		sql.append("create table if not exists " + nameTable + "(");
		for (int i = 0; i < column_number; i++) {
			if (i == column_number - 1) {
				sql.append("`" + (i + 1) + "` nvarchar(255) )");
			} else {
				sql.append("`" + (i + 1) + "` nvarchar(255),");
			}
		}
		// CONNECTION_STAGING.setAutoCommit(false);
		pre = CONNECTION_STAGING.prepareStatement(sql.toString());
		pre.executeUpdate();
		// CONNECTION_STAGING.commit();
	}

	public boolean addFileExcel(String path, String tableName, int column_number) throws Exception {
		boolean check = false;
		Workbook workbook = null;
		InputStream inputStream = null;

		PreparedStatement statement = null;
		CONNECTION_STAGING.setAutoCommit(false);
		// Tao cau query
		StringBuilder sql = new StringBuilder();
		sql.append("insert into " + tableName);
		sql.append(" value(");
		for (int i = 0; i < column_number; i++) {
			if (i == column_number - 1) {
				sql.append("?)");
				break;
			}
			sql.append("?,");
		}
		// Mo file xlsx bang thu vien poi
		inputStream = new FileInputStream(new File(path));
		workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		// Duyet iterator
		Iterator<Row> rowIterator = sheet.iterator();
		DataFormatter objDefaultFormat = new DataFormatter();
		// Bo qua dong dau tien vi la field name
		rowIterator.next();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			int index = column_number;
			// System.out.println(index);
			statement = CONNECTION_STAGING.prepareStatement(sql.toString());
			// Duyet vong for theo so column trong table config
			for (int i = 0; i < column_number; i++) {
				Cell cell = row.getCell(i);
				if (cell == null) {
					statement.setString(i + 1, "");
					index--;
				} else {
					switch (cell.getCellType()) {
					case NUMERIC:
						Double d = (Double) cell.getNumericCellValue();
						// Set gia tri preparestatement
						statement.setDouble(i + 1, d);
						break;
					case BLANK:
						// Set gia tri preparestatement
						statement.setString(i + 1, "");
						break;
					case STRING:
						// Set gia tri preparestatement
						statement.setString(i + 1, cell.toString());
						break;
					}
				}
			}
			// Thuc thi statement
			if (index != 1) {
				statement.executeUpdate();
				CONNECTION_STAGING.commit();
			} else {
				statement.close();
			}
		}
		System.out.println("add thanh cong");
		workbook.close();
		inputStream.close();
		return check;

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

	// Chuyen file den thu muc error
	public void moveFileToError(String file) throws Exception {
		File f = new File(file);
		String newPath = f.getParent() + File.separator + "Error" + File.separator + f.getName();
		Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
		BW.write("Move file " + f.getName() + " to folder error \r\n");
		BW.flush();
		System.out.println("move Error");
	}

	// Thuc hien chuyen file den thu muc success
	public void moveFileToSuccess(String file) throws Exception {
		File f = new File(file);
		String newPath = f.getParent() + File.separator + "Successfully" + File.separator + f.getName(); // path folder
		Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
		BW.write("Move file " + f.getName() + "  to folder successfully \r\n");
		System.out.println("move successfully");
	}

	public void loadToStaging(String path_dir_src, String file_name, String delimetter, int ignore_record,
			String file_type, String table_name_des, int column_number, boolean unzip) throws Exception {
		// Tao duong dan den file
		String path = path_dir_src + "//" + file_name; // path File
		PreparedStatement state = null;
		try {
			File file = new File(path);
			// Kiem tra type file
			if (file_type.equals("xlsx")) {
				// Chay ham load file xlsx
				addFileExcel(path, table_name_des, column_number);
			} else if(file_type.equals("txt") || file_type.equals("csv")) {
				// Chay ham file txt,csv
				String loadQuery = "LOAD DATA INFILE '" + path + "' INTO TABLE data FIELDS TERMINATED BY '\\"
						+ delimetter + "' LINES TERMINATED BY '\n' IGNORE " + ignore_record + " LINES";
				System.out.println(loadQuery);
				state = CONNECTION_STAGING.prepareStatement(loadQuery);
				CONNECTION_STAGING.setAutoCommit(false);
				// Thuc thi cau query
				state.executeUpdate();
				CONNECTION_STAGING.commit();
				state.close();
				System.out.println("Them data thanh cong");
			}else {
				throw new NullPointerException("Khong ho tro dinh dang file:"+file_type);
			}
			// Chuyen trang thai file thanh 'TF'
			changeStatusFile(file_name, "TF");
			// Chuyen file den thu muc successfully
			moveFileToSuccess(path);
			//send mail
			JavaMail.send(EMAIL, SUBJECT, "load file: "+file_name+"\n Thanh cong");
			//ghi logs
			BW.write("Them du lieu thanh cong \r\n");
			BW.flush();
		} catch (Exception e) {
			handleExcetion(e, file_name, path);
		}
	}

	public void changeStatusFile(String fileName, String status) throws SQLException, IOException {
		// TODO Auto-generated method stub
		// Tao cau query update logs
		String update = "UPDATE `data_config_log` SET status =? WHERE file_name=?";
		PreparedStatement statement = CONNECTION_CONTROL.prepareStatement(update);
		// set cac gia tri trong preparestatement
		statement.setString(1, status);
		statement.setString(2, fileName);
		statement.executeUpdate();
		statement.close();
		// Ghi logs
		BW.write("Thay doi status  thanh '" + status + "' \r\n");
		BW.flush();
	}

	public void insetDataAllFile() throws Exception {
		String destination, server_des, databasse, user_des, pwd_des, table_name_des, delimiter, file_type,
				path_dir_src, file_name, file_logs = null;
		int column_number = 0;
		boolean unzip = false;
		int ignore_record = 0;
		// Tao connection den database controll, neu khac null thi bo qua .
		if (CONNECTION_CONTROL == null) {
			CONNECTION_CONTROL = ConnectDB.getConectionControl("root", "0985153812");
		}
		// Tao cau truy van query
		String sql = "SELECT  destination,server_des, databasse,user_des,pwd_des,table_name_des, unzip, ignore_record,delimeter,file_type,path_dir_src,file_name,column_number ,file_logs from data_config inner join data_config_log"
				+ " on data_config_log.id = data_config.id where status = 'ER' limit 1";
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
			// Tao connection den database staging, neu khac null thi bo qua
			if (CONNECTION_STAGING == null) {
				CONNECTION_STAGING = ConnectDB.getConnection(destination, server_des, databasse, user_des, pwd_des);
			}
			// Tao file logs va doi tuong FileWriter ghi vao logs
			File file = new File(path_dir_src + "\\" + "logs" + "\\" + file_logs);
			System.out.println(file.getAbsolutePath());
			BW = new BufferedWriter(new FileWriter(file, true));
			BW.write("\r\n");
			BW.write("file: " + file_name + " " + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date())
					+ "\r\n");
			BW.flush();
			String path_file = path_dir_src + "//" + file_name;
			// Taoj table data neu chua co
			createTable(column_number, table_name_des);
			// load data vao staging
			loadToStaging(path_dir_src, file_name, delimiter, ignore_record, file_type, table_name_des, column_number,
					unzip);
			System.out.println("---------------------------------");
			BW.write("--------------------------------- \r\n");
			BW.flush();
		}
		BW.close();
		CONNECTION_CONTROL.close();
		CONNECTION_STAGING.close();
	}

	public void handleExcetion(Exception e, String fileName, String path) throws Exception {

		if (e instanceof SQLException) {
			//rollback du lieu
			CONNECTION_STAGING.rollback();
		}
		// chuyen doi trang thai
		changeStatusFile(fileName, "FAIL");
		// Chuyen file den thu muc error neu loi
		moveFileToError(path);
		//send mail
		 JavaMail.send(EMAIL, SUBJECT, "load file: "+fileName+"\n That bai \n Bug: "+ e);
		//ghi logs
		BW.write("Bug: " + e + "\r\n");
		BW.flush();
	}

	public static void main(String[] args) throws Exception {
		ExtractFileToStaging a = new ExtractFileToStaging();
		a.insetDataAllFile();
		System.out.println();
//

	}
}