package component_1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import common.ConnectDB;
import common.JavaMail;

public class DownloadFile {
	private String server;
	private String userName;
	private String password;
	private String remotePath;
	private String destinationPath;
	private String syntaxFileName;
	private int port;
	private static CkSsh ssh;
	private Connection connectionControl;
	private CheckFileName check;
	private String idConfig;
	private BufferedWriter logFile;

	public DownloadFile(String idConfig) {
		this.idConfig = idConfig;
		try {
			connectionControl = ConnectDB.getConectionControl("root", "");
			check = new CheckFileName();
		} catch (SQLException e) {
			e.printStackTrace();
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse", "I can't connect to database");
		}

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
		} catch (Exception e) {
			System.out.println("can not get information from database control");
			System.exit(0);
		}
	}

	public CkSsh connectServer() {
		CkGlobal glob = new CkGlobal();
		// mo khoa method
		glob.UnlockBundle("key");
		ssh = new CkSsh();
		// kết nối tới server qua port
		boolean success = ssh.Connect(server, port);
		if (!success) {
//			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File", "i can not access to server");
			System.out.println("can not access to server");
			System.exit(0);
		}
		// Đợi tối đa 5 giây khi đọc phản hồi
		ssh.put_IdleTimeoutMs(5000);
		// xac thuc user voi userName va password
		boolean connect = ssh.AuthenticatePw(userName, password);
		// neu connect khong thanh cong thi gui mail bao loi va ket thuc chuong trinh
		if (!connect) {
			// gui mail den "haunguyen0528@gmail.com" với subject "DataWarehouse- Download
			// file
			// va noi dung "i can not access to server with userName and pass da cho
			JavaMail.send("haunguyen0528@gmail.com", "DataWarehouse - Download File",
					"i can not access to server with userName: [" + this.userName + "] and pass [" + this.password
							+ "]");
			// ket thuc chuong trinh
			System.exit(0);
		}
		return ssh;
	}

	public String[] getListFileName() {
		String[] listFileNames = null;
		if (!server.equals("localhost")) {
			// mo channel de thuc hien cau lenh cmd
			int channelNum = ssh.OpenSessionChannel();
			// neu khong thanh cong thi in ra loi va dung chuong trinh
			if (channelNum < 0) {
				System.out.println("open channel failure");
				System.exit(0);
			}
			// cau lenh cmd de liet ke ten cac file co trong thu muc remote
			String cmd = "cd " + remotePath + "; ls";
			// thực hiện lệnh cmd trên channelNum được chỉ định
			ssh.SendReqExec(channelNum, cmd);
			// đọc dữ liệu đến channelNum cho đến khi máy chủ bị đóng
			ssh.ChannelReceiveToClose(channelNum);
			// trả về văn bản nhận được trên channelNum theo chartSet ansi
			String cmdResult = ssh.getReceivedText(channelNum, "utf-8");
			System.out.println(cmd);
			// chia kết quả trả về của câu lệnh theo "\n" để được mảng tên file
			listFileNames = cmdResult.split("\n");
		} else {
			File d = new File(remotePath);
			listFileNames = d.list();
		}
		return listFileNames;
	}

	/*
	 * download file trên server, insert vào table log
	 */
	public void downloadFileProcess() {
		System.loadLibrary("chilkat");
		// method lấy các thông tin cần thiết để thực hiện download process
		setup();
		// kết nối tới server để thực hiện download
		if (!server.equals("localhost")) {
			connectServer();
		}
		// lấy ra tên tất cả các file có trong thư mục remote
		String[] listFileNames = getListFileName();
		// lấy từng tên file trong mảng ra
		for (String fileName : listFileNames) {
			// kiểm tra tên file có đúng theo syntax lấy từ db không nếu đúng theo syntax
			// thì tiếp tục
			if (check.checkFileName(fileName, syntaxFileName)) {
				// kiểm tra file được download chưa
				boolean isDownload = checkFileDownloaded(fileName);
				// nếu chưa thì mới tiếp tục
				if (!isDownload) {
					// download file
					boolean downloaded = downloading(fileName);
					// nếu download file thành công thì insert vào table log
					if (downloaded) {

						// dùng map để lưu thông tin của file (tên file, đuôi file, số dòng ignore..)
						Map<String, String> infor = check.information();
						infor.put("fileName", fileName);
						// insert thông tin file vào table log
						insertLogTable(idConfig, infor);
					}
				}
			}
		}
		// ngắt kết nối tới server
		if (!server.equals("localhost")) {
			ssh.Disconnect();
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
			System.out.println(e.getMessage());
		}
	}

	// download file bằng api của chilkat
	public boolean downloading(String fileName) {
		boolean success = false;
		if (!server.equals("localhost")) {
			// gọi class CkScp của chilkat
			CkScp scp = new CkScp();
			// Sử dụng kết nối SSH của ssh để chuyển SCP.
			scp.UseSsh(ssh);
			// Tải tệp từ máy chủ SSH từ xa về local
			// nhận vào đường dẫn của file cần download trên server và đường dẫn lưu file ở
			// local
			success = scp.DownloadFile(remotePath + "\\" + fileName, destinationPath + "\\" + fileName);
		} else {
			File fileRemote = new File(remotePath + "\\" + fileName);
			File localFile = new File(destinationPath + "\\" + fileName);
			try {
				InputStream in = new BufferedInputStream(new FileInputStream(fileRemote));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
				byte[] buffer = new byte[1024];
				int lengthRead;
				while ((lengthRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, lengthRead);
					out.flush();
				}
				success = true;
			} catch (Exception e) {
				return success = false;
			}
		}
		if (success) {
			try {
				logFile.write("file: " + fileName + " " + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date())
						+ " download successfully\n");
				logFile.flush();
			} catch (IOException e) {
			}
		}
		return success;
	}

	/*
	 * kiểm tra xem file đã được download trước đó chưa
	 */
	public boolean checkFileDownloaded(String filename) {
		String sql = "select file_name from data_config_log where id+'" + this.idConfig + "' and file_name='" + filename
				+ "';";
		try {
			ResultSet rs = connectionControl.createStatement().executeQuery(sql);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;

	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		new DownloadFile(args[0]).downloadFileProcess();

	}
}