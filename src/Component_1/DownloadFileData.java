package Component_1;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class DownloadFileData {
	public static void main(String[] arg) throws JSchException {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession("guest_access", "drive.ecepvn.org", 2227);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword("123456");
			session.connect();
			System.out.println("connect");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.connect();
			System.out.println("ok");
			sftpChannel.get("volume1/ECEP/song.nguyen/DW_2020/data/sinhvien_chieu_nhom5.zip", "D:");
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}
}