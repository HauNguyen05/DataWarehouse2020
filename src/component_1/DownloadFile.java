package component_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Scanner;

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
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse", "I can't connect to database");
		}
		// step 1: get id config for process
		getIdConfig();
		// step 2: get necessary informations for process
		setup();
		// run process
		downloadFileProcess();
	}

	/*
	 * get id_config from user
	 */
	public void getIdConfig() {
		boolean run = true;
		while (run) {
			System.out.println("please enter idConfig:");
			Scanner sc = new Scanner(System.in);
			String id = sc.nextLine();
			if (id.equalsIgnoreCase("exit"))
				System.exit(0);
			String sql = "select id from data_config where id=?";
			PreparedStatement stmt;
			try {
				stmt = connectionControl.prepareStatement(sql);
				stmt.setString(1, id);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					this.idConfig = rs.getString(1);
					run = false;
					break;
				} else {
					System.out.println("id you enter not found, please enter again!");
				}

			} catch (SQLException e) {
				System.out.println("excute query fail");
			}
		}
	}

	/*
	 * connect table config to get information
	 */
	public void setup() {
		String sql = "SELECT server_src,user_src, pwd_src, port_src, path_remote, path_dir_src, syntax_file_name from data_config where id ="
				+ idConfig;
		try {
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
		} catch (Exception e) {
			System.out.println("can not get information from database control");
			System.exit(0);
		}
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
		return listFileNames;
	}

	public void downloadFileProcess() {
		if (connectServer() == null) {
			JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse - Connect to server",
					"I can't connect to server with account name: " + this.userName + " and password: "
							+ this.password);
		}
		String[] listFileNames = getListFileName();
		for (String fileName : listFileNames) {
			boolean isDownload = check.checkFileName(fileName, syntaxFileName);
			if (isDownload) {
				boolean downloaded = downloading(fileName);
				if (downloaded) {
					Map<String, String> infor = check.information();
					infor.put("fileName", fileName);
					insertLogTable(idConfig, infor);
				}
			}

		}
		JavaMail.send("haunguyen0528@gmail.com", "Data Warehouse - Download file", "Download data successfully");
		ssh.Disconnect();
	}

	public void insertLogTable(String idConfig, Map<String, String> infor) {
		String update = "INSERT INTO data_config_log(id, file_name,file_type, status, unzip) VALUES (?, ?,?,?, ?)";
		try {
			PreparedStatement statement = connectionControl.prepareStatement(update);
			statement.setString(1, idConfig);
			statement.setString(2, infor.get("fileName"));
			statement.setString(3, infor.get("typeFile"));
			statement.setString(4, "ER");
			statement.setString(5, infor.get("isUnzip"));
			statement.execute();
		} catch (Exception e) {
			System.out.println("can not insert into table log");
			System.exit(0);
		}
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
	}
}