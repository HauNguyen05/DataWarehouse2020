package component_1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkSFtp;
import com.chilkatsoft.CkSFtpDir;
import com.chilkatsoft.CkSFtpFile;

import common.ConnectDB;
import common.JavaMail;

public class DownloadFileSftp {
	private String server;
	private String userName;
	private String password;
	private String remotePath;
	private String destinationPath;
	private String syntaxFileName;
	private int port;
	private static CkSFtp sftp;
	private Connection connectionControl;
	private CheckFileName check;
	private String idConfig;
	private BufferedWriter logFile;

	public DownloadFileSftp(String idConfig) {
		this.idConfig = idConfig;
		check = new CheckFileName();
	}

	/*
	 * kết nối db control để lấy thông tin cần thiết: - server - tên user - mật khẩu
	 * của server - cổng kết nối - đường dẫn trên server - đường dẫn ở local -
	 * syntax của tên file
	 */
	public void setup() {
		String sql = "SELECT server_src,user_src, pwd_src, port_src, path_remote, path_dir_src, syntax_file_name from data_config where id ="
				+ idConfig;
		try {
			PreparedStatement statement = connectionControl.prepareStatement(sql);
			ResultSet r = statement.executeQuery();
			if (r.next()) {
				this.server = r.getString(1);
				this.userName = r.getString(2);
				this.password = r.getString(3);
				this.port = Integer.parseInt(r.getString(4));
				this.remotePath = r.getString(5);
				this.destinationPath = r.getString(6);
				this.syntaxFileName = r.getString(7);
			}
			// đường dẫn chứa file logs của id config
			File pathDir = new File(destinationPath + "/" + "logs");
			// kiểm tra nếu thư mục chưa có thì tạo ra
			if (!pathDir.exists()) {
				pathDir.mkdir();
			}
			// tạo file log để ghi lại file nào được download
			File file = new File(pathDir + "/LogDownloadConfig" + idConfig + ".txt");
			logFile = new BufferedWriter(new FileWriter(file, true));
			statement.close();
		} catch (Exception e) { // nếu bị lỗi thì gửi mail báo và kết thúc chương trình
			System.out.println("can not get information from database control");
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse", "can not get information from database control");
			System.exit(0);
		}
	}

	public CkSFtp connectServer() {
		CkGlobal glob = new CkGlobal();
		// mo khoa method
		glob.UnlockBundle("key");
		sftp = new CkSFtp();
		// chờ 5 giây khi kết nối với máy chủ sftp
		sftp.put_ConnectTimeoutMs(5000);
		// Đợi tối đa 5 giây khi đọc phản hồi
		sftp.put_IdleTimeoutMs(5000);
		// kết nối tơi server qua địa chỉ server và port
		boolean success = sftp.Connect(server, port);
		// nếu kết nối lỗi
		if (!success) {
			// gửi mail báo lỗi
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File", "i can not access to server");
			System.out.println("can not access to server");
			// kết thúc chương trình.
			System.exit(0);
		}
		// xác thực user với userName và password
		success = sftp.AuthenticatePw(userName, password);
		// nếu xác thực lỗi
		if (!success) {
			// gui mail den "haunguyen0528@gmail.com" với subject "DataWarehouse- Download
			// file
			// va noi dung "i can not access to server with userName and pass da cho
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File",
					"i can not access to server with userName: [" + this.userName + "] and pass [" + this.password
							+ "]");
			// ket thuc chuong trinh
			System.exit(0);
		}

		// khởi tạo sftp
		success = sftp.InitializeSftp();
		// khởi tạo thất bại thì gửi mail và kết thúc chương trình
		if (!success) {
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File", "can not initializeSftp");
			// ket thuc chuong trinh
			System.exit(0);
		}
		return sftp;
	}

	public List<String> getListFileName() {
		List<String> list = new ArrayList<String>();
		// mở thư mục remotePath
		String handle = sftp.openDir(remotePath);
		// nếu không mở được thư mục thì gửi mail báo lỗi và dừng chương trình
		if (sftp.get_LastMethodSuccess() != true) {
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File",
					"can not open directory " + remotePath);
			System.exit(0);
		}
		// đọc file trong thư mục remote
		CkSFtpDir dirListing = sftp.ReadDir(handle);
		// nếu đọc không được thì gửi mail báo lỗi và dừng chương trình
		if (sftp.get_LastMethodSuccess() != true) {
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File",
					"can not open directory " + remotePath);
			System.exit(0);
		}
		// tổng số file trong thư mục
		int n = dirListing.get_NumFilesAndDirs();
		for (int j = 0; j < n; j++) {
			// lấy ra file
			CkSFtpFile fileObj = dirListing.GetFileObject(j);
			// add tên file vào list
			list.add(fileObj.filename());
		}
		return list;
	}

	/*
	 * download file trên server, insert vào table log
	 */
	public void downloadFileProcess() {
		// bước 1: kết nối database control
		try {
			connectionControl = ConnectDB.getConectionControl("root", "");
		} catch (SQLException e) {
			System.out.println("can not connect to database control");
			// nếu lỗi kết nối thì gửi mail báo
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse", "I can't connect to database");
			// kết thúc chương trình
			System.exit(0);
		}
		// bước 2: lấy các thông tin cần thiết để thực hiện download process
		setup();
		// bước 3: kết nối tới server để thực hiện download
		System.loadLibrary("chilkat");
		connectServer();
		// bước 4: lấy ra tên tất cả các file có trong thư mục remote
		List<String> listFileNames = getListFileName();
		// bước 5: kiểm tra còn file trong list không
		while (!listFileNames.isEmpty()) {
			// bước 6: lấy ra 1 tên file và xóa nó trong list
			String fileName = listFileNames.remove(0);
			// bước 7: kiểm tra tên file có đúng theo syntax lấy từ db không?
			// nếu đúng theo syntax thì tiếp tục
			if (check.checkFileName(fileName, syntaxFileName)) {
				// bước 8: kiểm tra file được download chưa
				boolean isDownloaded = checkFileDownloaded(fileName);
				// nếu chưa thì mới tiếp tục
				if (!isDownloaded) {
					// bước 9: download file
					boolean downloaded = downloading(fileName);
					// nếu download file thành công thì ghi log và insert vào table log
					if (downloaded) {
						// bước 10: ghi vào file log là download file thành công
						writeLog("Download file ", fileName, "successfully");
						// dùng map để lưu thông tin của file (tên file, đuôi file, số dòng ignore..)
						Map<String, String> infor = check.information();
						infor.put("fileName", fileName);
						// bước 11: insert thông tin file vào table log
						insertLogTable(idConfig, infor);
					}
				}
			}
		}
		// bước 12: ngắt kết nối tới server
		sftp.Disconnect();
	}

	/*
	 * viết vào file log
	 */
	private void writeLog(String subject, String fileName, String status) {
		try {
			logFile.write(subject + " " + fileName + " on "
					+ new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date()) + " " + status + "\n");
			logFile.flush();
		} catch (IOException e) {
			System.out.println("write log file fails");
		}
	}

	/*
	 * thêm thông tin của file vừa download về vào table log
	 */
	public void insertLogTable(String idConfig, Map<String, String> infor) {
		String update = "INSERT INTO data_config_log(id, file_name,file_type, status, unzip, ignore_record) VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = connectionControl.prepareStatement(update);
			statement.setString(1, idConfig);
			statement.setString(2, infor.get("fileName"));
			statement.setString(3, infor.get("typeFile"));
			statement.setString(4, "ER");
			statement.setString(5, infor.get("isUnzip"));
			statement.setString(6, infor.get("ignore"));
			statement.execute();
		} catch (Exception e) {
			System.out.println("insert fail");
			// ghi vào file log là insert file fails. với lỗi e.getMessage()
			writeLog("Insert file ", infor.get("fileName"), "fails\n");

		}
	}

	// download file bằng api của chilkat
	public boolean downloading(String fileName) {
		boolean success = false;
		// mở file để download
		String handle = sftp.openFile(remotePath + fileName, "readOnly", "openExisting");
		// Download file
		success = sftp.DownloadFile(handle, destinationPath + fileName);
		if (!success) {
			System.out.println(sftp.lastErrorText());
			return false;
		} else {
			// đóng file đã download
			success = sftp.CloseHandle(handle);
			return true;
		}
	}

	/*
	 * kiểm tra xem file đã được download trước đó chưa
	 */
	public boolean checkFileDownloaded(String filename) {
		String sql = "select file_name from data_config_log where id+'" + this.idConfig + "' and file_name='" + filename
				+ "';";
		try {
			// thực hiện câu query và lưu kết quả trong resultSet
			ResultSet rs = connectionControl.createStatement().executeQuery(sql);
			// nếu có kết quả thì trả về true không thì trả về false
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;

	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new DownloadFileSftp("3").downloadFileProcess();

	}
}
