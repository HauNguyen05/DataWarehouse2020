package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExtractFileToStaging {

// ket noi database
	public Connection getConnection(String destination, String server, String nameDatabasse, String userName,
			String password, String tableName) {
		try {
			Class.forName(server);
			String url = destination + nameDatabasse;
			return DriverManager.getConnection(url, userName, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("kết nối connection failure!");
			return null;
		}

	}

	public Connection connectionConfig() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/warehouse_control";
			return DriverManager.getConnection(url, "root", "0985153812");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("kết nối connectionConfig failure!");
			return null;
		}

	}

//	public void unzip(String file) {
//		try {
//			File f = new File(file);
//			if (f.exists()) {
//				System.out.println("Zip exists");
//				System.out.println(f.getAbsolutePath());
//			}
//			ZipArchiveInputStream archive = new ZipArchiveInputStream(
//					new BufferedInputStream(new FileInputStream(f)));
//
//			ArchiveEntry entry = archive.getNextEntry();
//				System.out.println(entry);
//			byte[] b = new byte[2 * 1024];
//			//while (ze != null) {
////				String fileName = ze.getName();
////			//	FileOutputStream fos = new FileOutputStream(f.getParent() + File.separator + fileName);
////				int a;
////				while ((a = zis.read(b)) != -1) {
////					fos.write(b, 0, a);
////				}
////				fos.close();
////				zis.closeEntry();
////				ze = zis.getNextEntry();
//		//	}
//			System.out.println("unzip successfully");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.out.println("unzip failure");
//		}
//	}
	public void addFileExcel(Connection connectionStagingDB, String file) {
		try {
			InputStream inputStream = new FileInputStream(new File(file));
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			// Get iterator to all the rows in current sheet
			Iterator<Row> rowIterator = sheet.iterator();
			// Traversing over each row of XLSX file
			Row r =rowIterator.next();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				List<String> data = new ArrayList<String>();
				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					String item =cellIterator.next().toString();
					System.out.println(item);
					if(item.equals("")) {
						data.add("null");
					}else {
						data.add(item);
					}
					
				}
				//add data to database staging
				addData(data,connectionStagingDB);
			}
		} catch (Exception e) {
			System.out.println("poi failure");
			return;
		}
	}

	public void addData(List<String> data,Connection connectionStagingDB ) throws ClassNotFoundException {

		PreparedStatement statement = null;
		try {
			connectionStagingDB.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			sql.append("insert into data(");
			for (int i=0;i< data.size();i++) {
				if(i==data.size()-1) {
					sql.append("`"+(i+1)+"`)");
					break;
				}
				sql.append("`"+(i+1)+"`,");
			}
			
			sql.append(" value(");
			for (int i=0;i< data.size();i++) {
				if(i==data.size()-1) {
					sql.append("?)");
					break;
				}
				sql.append("?,");
			}
			
			
			statement =connectionStagingDB.prepareStatement(sql.toString());
			for (int i=0;i< 11;i++) {
				statement.setString(i+1, data.get(i));
			}
			statement.executeUpdate();
			connectionStagingDB.commit();
			System.out.println("add 1 row ok");
		} catch (SQLException e1) {
			if (connectionStagingDB != null) {
				try {
					connectionStagingDB.rollback();
					System.out.println(" addData khong thanh cong");
				} catch (SQLException e11) {
					e11.printStackTrace();
				}
			}
		}

	}

	public void moveFileToError(String file) {
		File f = new File(file);
		String newPath = f.getParent() + File.separator + "Error" + File.separator + f.getName();
		try {
			Files.move(Paths.get(file), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Move file " + f.getName() + " to folder error");
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
			System.out.println("Move file " + f.getName() + "  to folder successfully");
		} catch (IOException e) {
			System.out.println("Move file thanh bai");
		}
	}
	
	// Load data File CSV to Staging
	public void loadToStaging(Connection connectionStagingDB, String path_dir_src, String file_name, String delimetter,
			int ignore_record, String file_type) {
		String path = path_dir_src + "//" + file_name; // path File
		File file = new File(path);
		if(!file.exists()) {
			System.out.println("file not found");
			return;
		}
		try {
			
			if (file_type.equals("xlsx")) {
				addFileExcel(connectionStagingDB, path);
			} else {

				String loadQuery = "LOAD DATA INFILE '" + path + "' INTO TABLE data FIELDS TERMINATED BY '\\"
						+ delimetter + "' LINES TERMINATED BY '\n' IGNORE " + ignore_record + " LINES";
				PreparedStatement state = connectionStagingDB.prepareStatement(loadQuery);
				state.executeUpdate();
				state.close();
				System.out.println("Them data thanh cong");
			}
			// chuyen doi trang thai file
			//changeStatusFile(file_name, "TF");
			// chuyen file den thu muc thanh cong
		//	moveFileToSuccess(path);
		} catch (Exception e) {
			System.out.println("Them data that bai");
			// chuyen file den thu muc error
		//	moveFileToError(path);
		}

	}

	public void changeStatusFile(String fileName, String status) throws SQLException {
		// TODO Auto-generated method stub
		Connection connection = connectionConfig();
		String update = "UPDATE `data_config_log` SET status =? WHERE file_name=?";
		PreparedStatement statement = connection.prepareStatement(update);
		statement.setString(1, status);
		statement.setString(2, fileName);
		statement.executeUpdate();
		System.out.println("Thay doi status  thanh '" + status + "'");
	}

	public void insetDataAllFile() throws SQLException, IOException {
		String destination, server_des, databasse, user_des, pwd_des, table_name_des, delimiter, file_type,
				path_dir_src, file_name;
		boolean unzip;
		int ignore_record;

		Connection connection = connectionConfig();
		String sql = "SELECT  destination,server_des, databasse,user_des,pwd_des,table_name_des, unzip, ignore_record,delimeter,file_type,path_dir_src,file_name from data_config inner join data_config_log"
				+ " on data_config_log.id = data_config.id where status = 'ER'";
		PreparedStatement statement1 = connection.prepareStatement(sql);
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
			Connection connectionStagingDB = getConnection(destination, server_des, databasse, user_des, pwd_des,
					table_name_des);
			loadToStaging(connectionStagingDB, path_dir_src, file_name, delimiter, ignore_record, file_type);
			System.out.println("---------------------------------");
		}
	}

	public static void main(String[] args) throws SQLException, IOException {
		ExtractFileToStaging a = new ExtractFileToStaging();
		 a.insetDataAllFile();
		// a.moveFileToError("E:\\warehouse2020\\sinhvien_chieu_nhom1.txt");
		//a.unzip("E://warehouse2020//sinhvien_chieu_nhom5.zip");

	}

}
