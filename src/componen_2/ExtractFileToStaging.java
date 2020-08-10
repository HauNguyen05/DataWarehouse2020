package componen_2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public ExtractFileToStaging(int id) throws Exception {
		insetDataAllFile(id);
	}
// tao table data
//	public void createTable(int column_number, String nameTable) throws Exception {
//		PreparedStatement pre = null;
//		// Tao cau query
//		StringBuilder sql = new StringBuilder();
//		sql.append("create table if not exists " + nameTable + "(");
//		for (int i = 0; i < column_number; i++) {
//			if (i == column_number - 1) {
//				sql.append("`" + (i + 1) + "` nvarchar(255) )");
//			} else {
//				sql.append("`" + (i + 1) + "` nvarchar(255),");
//			}
//		}
//		// CONNECTION_STAGING.setAutoCommit(false);
//		pre = CONNECTION_STAGING.prepareStatement(sql.toString());
//		pre.executeUpdate();
//		// CONNECTION_STAGING.commit();
//	}
	public void truncateTable(String nameTable) throws SQLException {
		PreparedStatement pre = null;
		// Tao cau query
		StringBuilder sql = new StringBuilder();
		sql.append("truncate " + nameTable );
		pre = CONNECTION_STAGING.prepareStatement(sql.toString());
		pre.executeUpdate();
		pre.close();
	}
//10. Load dữ liệu từng dòng
	public boolean addFileExcel(String path, String tableName, int column_number ) throws Exception {
		boolean check = false;
		Workbook workbook = null;
		InputStream inputStream = null;
		int countLine=0;
		try {
			PreparedStatement statement = null;
			CONNECTION_STAGING.setAutoCommit(false);
			// Tao cau query
			StringBuilder sql = new StringBuilder();
			sql.append("insert into " + tableName+"(");
			for (int i = 0; i < column_number; i++) {
				if (i == column_number - 1) {
					sql.append("`" + (i + 1) + "`)");
					break;
				}
				sql.append("`" + (i + 1) + "`,");
			}
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
					if (cell == null || cell.equals(" ") ||cell.toString().isEmpty()) {
						statement.setString(i + 1, "");
						index--;
					} else {
						switch (cell.getCellType()) {
						case NUMERIC:
							int d = (int) cell.getNumericCellValue();
							// Set gia tri preparestatement
							statement.setInt(i + 1, d);
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
				if (index <= 1) {
					statement.close();
					break;
				} else {
					countLine++;
					statement.executeUpdate();
					CONNECTION_STAGING.commit();
					
				}
			}
//	12. Kiểm tra load dữ liệu			
			int countLineDB =countLineDB(tableName);
			if (countLineDB != countLine) {	// kiem tra so dong co bang nhau khong
				truncateTable(tableName);
				throw new SQLException("them du lieu khong du dong");
			}
			System.out.println("add thanh cong");

			return check;
		} finally {
			workbook.close();
			inputStream.close();
		}
	}


	// Chuyen file den thu muc error
	public void moveFileToError(String file) throws Exception {
		File f = new File(file);
		File parent = new File(f.getParent() + File.separator + "Error");
		parent.mkdir();
		String newPath = parent + File.separator + f.getName();
		Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
		BW.write("Move file " + f.getName() + " to folder error \r\n");
		BW.flush();
		System.out.println("move Error");
	}

	// Thuc hien chuyen file den thu muc success
	public void moveFileToSuccess(String file) throws Exception {
		File f = new File(file);
		File parent = new File(f.getParent() + File.separator + "Successfully");
		parent.mkdir();
		String newPath = parent + File.separator + f.getName();
		Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
		BW.write("Move file " + f.getName() + "  to folder successfully \r\n");
		System.out.println("move successfully");
	}

	public void loadToStaging(String path_dir_src, String file_name, String delimetter, int ignore_record,
			String file_type, String table_name_des, int column_number, boolean unzip) throws Exception {
		// Tao duong dan den file
		String path = path_dir_src + "//" + file_name; // path File
		PreparedStatement state = null;
		int countLineFile = 0;
		int countAfterAddFile = 0;
		try {
// 8. Kiểm tra file exist
			File file = new File(path);
			// kiem tra file ton tai hay khong
			if (!file.exists())
				throw new FileNotFoundException("file khong ton tai");
			// dem so dong trong file
			countLineFile = countLineFile(path);
// 9. Kiểm tra file type
			
			// Kiem tra type file
			if (file_type.equals("xlsx")) {
// 10. Load dữ liệu từng dòng
				addFileExcel(path, table_name_des, column_number);
			}
			if (file_type.equals("txt") || file_type.equals("csv")) {
// 11. load nhanh hết tất cả các dòng 					
				try {
				// Chay ham file txt,csv
				String loadQuery = "LOAD DATA INFILE '" + path + "' INTO TABLE "+table_name_des+" FIELDS TERMINATED BY '\\"
						+ delimetter + "' LINES TERMINATED BY '\n' IGNORE " + ignore_record +" LINES ";
				state = CONNECTION_STAGING.prepareStatement(loadQuery);
				CONNECTION_STAGING.setAutoCommit(false);
				// Thuc thi cau query
				state.executeUpdate();

				countAfterAddFile = countLineDB(table_name_des);// dem so dong trong staging sau khi add file
				System.out.println("countAfterAddFile : " + countAfterAddFile);
// 13. Kiểm tra số dòng 				
				if (countLineFile != countAfterAddFile) {	// kiem tra so dong co bang nhau khong
					throw new SQLException("them du lieu khong du dong");
				}

				CONNECTION_STAGING.commit();
				state.close();
				System.out.println("Them data thanh cong");
				}catch (Exception e) {
					CONNECTION_STAGING.rollback();
				}
// 12. Kiểm tra load dữ liệu
			} else if(file_type.equals("zip")) {
				throw new Exception("Khong ho tro dinh dang file:" + file_type);
			}
// 16. Ghi logs, chuyển status thành 'TF', move file to folder Success
			// Chuyen trang thai file thanh 'TF'
			changeStatusFile(file_name, "TF");
			// Chuyen file den thu muc successfully
			moveFileToSuccess(path);
	
			BW.write("Them du lieu thanh cong \r\n");
			BW.flush();

		} catch (Exception e) {
			System.out.println(e);
			handleExcetion(e, file_name, path);
		}
	}

	public void changeStatusFile(String fileName, String status) throws SQLException, IOException {
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

	public void insetDataAllFile(int idConfig) throws Exception {
		String destination, server_des, databasse, user_des, pwd_des, table_name_des, delimiter, file_type,
				path_dir_src, file_name, file_logs = null;
		int column_number = 0;
		boolean unzip = false;
		int ignore_record = 0;
		ResultSet r = null;
		try {
// 1 .Connect database control			
			// Tao connection den database controll, neu khac null thi bo qua .
			if (CONNECTION_CONTROL == null) {
				CONNECTION_CONTROL = ConnectDB.getConectionControl("root", "");
			}
// 3. Lay 1 record co idConfig = idConfig va co status ="ER"		
			// Tao cau truy van query
			String sql = "SELECT  destination,server_des, databasse,user_des,pwd_des,table_name_des, unzip, ignore_record,delimeter,file_type,path_dir_src,file_name,column_number ,file_logs from data_config inner join data_config_log"
					+ " on data_config_log.id = data_config.id where data_config_log.id="+idConfig+" and status = 'ER' limit 1";
			PreparedStatement statement1 = CONNECTION_CONTROL.prepareStatement(sql);
			r = statement1.executeQuery();
			if (r.next()) {
				destination = r.getString(1);
				server_des = r.getString(2);
				databasse = r.getString(3);
				user_des = r.getString(4);
				pwd_des = r.getString(5);
				table_name_des = r.getString(6);
				ignore_record = Integer.valueOf(r.getString(8));
				delimiter = r.getString(9);
				file_type = r.getString(10);
				path_dir_src = r.getString(11);
				file_name = r.getString(12);
				column_number = Integer.valueOf(r.getString(13));
				file_logs = r.getString(14);
// 4 .Ket noi database staging				
				// Tao connection den database staging, neu khac null thi bo qua
				if (CONNECTION_STAGING == null) {
					CONNECTION_STAGING = ConnectDB.getConnection(destination, server_des, databasse, user_des, pwd_des);
				}
// 6. Tạo file logs				
				// Tao file logs va doi tuong FileWriter ghi vao logs
				createFileLogs(path_dir_src, file_logs, file_name);
				System.out.println(file_name);
// 7. Truncate du lieu trong table data				
				//xoa du lieu table
				truncateTable(table_name_des);
			
				// load data vao staging
				loadToStaging(path_dir_src, file_name, delimiter, ignore_record, file_type, table_name_des,
						column_number, unzip);
				BW.write("--------------------------------- \r\n");
				BW.flush();
			}else {
				System.out.println("không tìm thấy file có trang thai 'ER'");
			}
			statement1.close();
		} finally {

			if (BW != null) {
				BW.close();
			} else if (CONNECTION_CONTROL != null) {
				CONNECTION_CONTROL.close();
			} else if (CONNECTION_STAGING != null) {
				CONNECTION_STAGING.close();
			}
		}

	}

	public void handleExcetion(Exception e, String fileName, String path) throws Exception {
// 14. Ghi logs, chuyển  status thành 'FAIL', move file to folder Error		
		// chuyen doi trang thai
		changeStatusFile(fileName, "FAIL");
// 15. Send mail báo lỗi		
		// send mail
		JavaMail.send(EMAIL, SUBJECT, "load file: " + fileName + "\nThat bai \nBug: " + e);
		// ghi logs
		BW.write("Bug: " + e + "\r\n");
		BW.flush();
		if (e instanceof FileNotFoundException) {
			return;
		}
		// Chuyen file den thu muc error neu loi
		moveFileToError(path);

	}

	public int countLineFile(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
		br.readLine();
		int count = 0;
		String s="";
		while ((s=br.readLine()) != null) {
			count++;
		}
		br.close();
		return count;
	}

	public int countLineDB(String table) throws SQLException {
		ResultSet r = null;
		PreparedStatement pre = null;
		int count = 0;
		String sql = "select count(`1`) as count from " + table;
		pre = CONNECTION_STAGING.prepareStatement(sql);
		r = pre.executeQuery();
		while (r.next()) {
			count = Integer.valueOf(r.getString(1));
		}
		pre.close();
		r.close();
		return count;
	}

	public void createFileLogs(String pathDirSrc, String fileLogs, String fileName) throws IOException {
		File parent = new File(pathDirSrc + "\\" + "logs");
		parent.mkdir();
		File file = new File(parent + "\\" + fileLogs);
		BW = new BufferedWriter(new FileWriter(file, true));
		BW.write("\r\n");
		BW.write("file: " + fileName + " " + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date()) + "\r\n");
		BW.flush();
	}

	public static void main(String[] args) throws Exception {
		ExtractFileToStaging a = new ExtractFileToStaging(3);
	}
	
}