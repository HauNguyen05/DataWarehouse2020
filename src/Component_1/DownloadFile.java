package Component_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkScp;
import com.chilkatsoft.CkSsh;

import Common.ConnectDB;

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
	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public DownloadFile() {
		try {
			connectionControl = ConnectDB.getConectionControl("root", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * kết nối table config lấy các field thông tin kết nối server
	 */
	public void setup(String nameConfig) throws SQLException {
		String sql = "SELECT server_src,user_src, pwd_src, port_src, path_dir_src, destination, syntax_file_name from data_config where id ="
				+ nameConfig;
		PreparedStatement statement = connectionControl.prepareStatement(sql);
		ResultSet r = statement.executeQuery();
		while (r.next()) {
			this.server = r.getString(1);
			this.userName = r.getString(2);
			this.password = r.getString(3);
			this.port = Integer.parseInt(r.getString(4));
			this.remotePath = r.getString(5);
			this.destinationPath = r.getString(6);
			this.syntaxFileName = r.getString(7);
		}
		statement.close();
	}

	public CkSsh connectServer() {
		CkGlobal glob = new CkGlobal();
		glob.UnlockBundle("key");
		glob.get_UnlockStatus();
		ssh = new CkSsh();
		ssh.Connect(server, port);
		ssh.put_IdleTimeoutMs(5000);
		ssh.AuthenticatePw(userName, password);
		return ssh;
	}

	public String[] getListFileName() {
		// Mở kênh để gửi lệnh qua
		int channelNum = ssh.OpenSessionChannel();
		// Nếu mở kênh bị lỗi thì hiện lỗi
		if (channelNum < 0) {
			System.out.println("Lỗi mở kênh");
			System.exit(0);
		}
		// Câu lệnh list ra tất cả tên file có trong folder data
		String cmd = "cd " + remotePath + "; ls";
		ssh.SendReqExec(channelNum, cmd);
		// Gọi Channel để đọc đầu ra cho đến khi nhận được lệnh đóng kênh của máy chủ.
		ssh.ChannelReceiveToClose(channelNum);
		// Lưu kết quả
		String cmdResult = ssh.getReceivedText(channelNum, "ansi");
		System.out.println(cmdResult);
		String[] listFileNames = cmdResult.split("\n");
		return listFileNames;
	}

	public void downloadFileProcess() throws SQLException {
		setup("1");
		connectServer();
		String[] listFileNames = getListFileName();
		for (String fileName : listFileNames) {
			boolean isDownload = CheckFileName.checkFileName(fileName, syntaxFileName);
			if (isDownload) {
				boolean a = downloading(fileName);
				System.out.println("download file " + fileName +" " +a );
			}

		}
		ssh.Disconnect();
	}

	public boolean downloading(String fileName) {
		CkScp scp = new CkScp();
		boolean success = scp.UseSsh(ssh);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return false;
		}
		success = scp.DownloadFile(remotePath+fileName, destinationPath+fileName);
		
		return success;
	}

	public static void main(String[] args) throws SQLException {
		DownloadFile c = new DownloadFile();
		c.downloadFileProcess();
	}
}