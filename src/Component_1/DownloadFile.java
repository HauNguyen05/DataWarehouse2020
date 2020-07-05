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
			check = new CheckFileName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * connect table config to get information
	 */
	public void setup(String nameConfig) throws SQLException {
		String sql = "SELECT server_src,user_src, pwd_src, port_src, path_remote, path_dir_src, syntax_file_name from data_config where id ="
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
		// open channel to excute cmd
		int channelNum = ssh.OpenSessionChannel();
		// if fail print fail
		if (channelNum < 0) {
			System.out.println("open channel failure");
			System.exit(0);
		}
		// cmd list all file in folder
		String cmd = "cd " + remotePath + "; ls";
		ssh.SendReqExec(channelNum, cmd);
		ssh.ChannelReceiveToClose(channelNum);
		String cmdResult = ssh.getReceivedText(channelNum, "ansi");
		String[] listFileNames = cmdResult.split("\n");
		System.out.println(listFileNames.length);
		return listFileNames;
	}

	public void downloadFileProcess(String idConfig) throws SQLException {
		setup(idConfig);
		connectServer();
		String[] listFileNames = getListFileName();
		for (String fileName : listFileNames) {
			boolean isDownload = check.checkFileName(fileName, syntaxFileName);
			if (isDownload) {
				boolean downloaded = downloading(fileName);
				if (downloaded) {
					Map<String, String> infor = check.information();
					infor.put("fileName", fileName);
					insertLogTable(idConfig, infor);
					System.out.println("insert log success");
				}
			}

		}
		ssh.Disconnect();
	}

	public void insertLogTable(String idConfig, Map<String, String> infor) throws SQLException {
		String update = "INSERT INTO data_config_log(id, file_name,file_type, status, unzip) VALUES (?, ?,?,?, ?)";
		PreparedStatement statement = connectionControl.prepareStatement(update);
		statement.setString(1, idConfig);
		statement.setString(2, infor.get("fileName"));
		statement.setString(3, infor.get("typeFile"));
		statement.setString(4, "ER");
		statement.setString(5, infor.get("isUnzip"));
		statement.execute();
	}

	public boolean downloading(String fileName) {
		CkScp scp = new CkScp();
		boolean success = scp.UseSsh(ssh);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return false;
		}
		success = scp.DownloadFile(remotePath + fileName, destinationPath + fileName);

		return success;
	}

	public static void main(String[] args) throws SQLException {
		DownloadFile c = new DownloadFile();
		c.downloadFileProcess("1");
	}
}