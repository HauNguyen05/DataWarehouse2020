package Component_1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DownloadFile {
	private static Session session;
	static final String host = "drive.ecepvn.org";
	static final String userName = "guest_access";
	static final String password = "123456";
	static final String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/";
	static final int port = 2227;

	public static void main(String[] arg) throws JSchException {
		DownloadFile DF = new DownloadFile();
		String[] syntaxs = { "sinhvien_sang_nhom", "sinhvien_chieu_nhom" };
		String remoteSource = DownloadFile.remotePath + "data";
		ArrayList<String> fileNames = DF.getFileNameDownload(session, remoteSource, syntaxs);
		System.out.println("size: " + fileNames.size());
//		for (String fileName : fileNames) {
//			System.out.println(fileName);
//		}
//		DF.downloadFile(fileNames, "D:\\DataWarehouse2020\\data\\source\\");
		session.disconnect();
		System.exit(0);
	}

	public DownloadFile() {
		getSession();
	}

	public Session getSession() {
		if (session == null) {
			JSch jsch = new JSch();
			try {
				session = jsch.getSession(userName, host, port);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return session;
	}

	public ArrayList<String> getFileNameDownload(Session session, String remoteSource, String[] syntaxs) {
		String command = "cd " + remoteSource + ";ls";
		System.out.println(command);
		Channel channel = null;
		ArrayList<String> fileNames = new ArrayList<String>();
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String result = new String(tmp, 0, i);
					String[] listfileName = result.split("\n");
					System.out.println(listfileName.length);
					for (int j = 0; j < listfileName.length; j++) {
						String fileName = listfileName[j];
						String[] isFile = fileName.split("\\.");
						if (isFile.length > 1) {
							boolean isDownload = CheckFileName.checkFileName(fileName, syntaxs);
							if (isDownload) {
								fileNames.add(fileName);
							}
						}
					}
				}
				if (channel.isClosed()) {
					System.out.println("exit status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
					System.out.println(ee.getMessage());
				}
			}
			channel.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNames;
	}

	public void downloadFile(ArrayList<String> fileNames, String des) {
		String command = "scp " + remotePath + "data/" + fileNames.get(0) + " " + des + fileNames.get(0);
		System.out.println(command);
		Channel channel = null;
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("connect shell");
			channel.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("lỗi " + e.getMessage());
		}
	}
}