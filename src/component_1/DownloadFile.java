package component_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	 * kết nối db control để lấy thông tin cần thiết:
	 * - server
	 * - tên user
	 * - mật khẩu của server
	 * - cổng kết nối
	 * - đường dẫn trên server
	 * - đường dẫn ở local
	 * - syntax của tên file
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
		if(!success) {
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
			// gui mail den "haunguyen0528@gmail.com" với subject "DataWarehouse- Download file
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
		//đọc dữ liệu đến channelNum cho đến khi máy chủ bị đóng
		ssh.ChannelReceiveToClose(channelNum);
		// trả về văn bản nhận được trên channelNum theo chartSet ansi
		String cmdResult = ssh.getReceivedText(channelNum, "utf-8");
		System.out.println(cmd);
		//chia kết quả trả về của câu lệnh theo "\n" để được mảng tên file
		String[] listFileNames = cmdResult.split("\n");
		return listFileNames;
	}

	/*
	 * download file trên server, insert vào table log
	 */
	public void downloadFileProcess() {
		System.loadLibrary("chilkat");
		//method lấy các thông tin cần thiết để thực hiện download process
		setup();
		// kết nối tới server để thực hiện download
		connectServer();
		// lấy ra tên tất cả các file có trong thư mục remote
		String[] listFileNames = getListFileName();
		// lấy từng tên file trong mảng ra
		for (String fileName : listFileNames) {
			// kiểm tra nếu file chưa được download thì mới tiếp tục
			if (!checkFileDownloaded(fileName)) {
				// kiểm tra tên file có đúng theo syntax lấy từ db không
				boolean isDownload = check.checkFileName(fileName, syntaxFileName);
				// nếu đúng theo syntax thì download
				if (isDownload) {
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
		ssh.Disconnect();
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
		//gọi class CkScp của chilkat
		CkScp scp = new CkScp();
		//Sử dụng kết nối SSH của ssh để chuyển SCP.
		scp.UseSsh(ssh);
		//Tải tệp từ máy chủ SSH từ xa về local
		// nhận vào đường dẫn của file cần download trên server và đường dẫn lưu file ở local
		boolean  success = scp.DownloadFile(remotePath + "\\" + fileName, destinationPath + "\\" + fileName);
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

	public static void main(String[] args) {
		new DownloadFile("1").downloadFileProcess();
		
	}
}