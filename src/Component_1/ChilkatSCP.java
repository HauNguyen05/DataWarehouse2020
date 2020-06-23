package Component_1;

import com.chilkatsoft.*;

public class ChilkatSCP {

	static {
		try {
			System.loadLibrary("chilkat");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}
	}

	public static void main(String argv[]) {
		CkGlobal glob = new CkGlobal();
		glob.UnlockBundle("Anything for 30-day trial");
		glob.get_UnlockStatus();
		CkSsh ssh = new CkSsh();

		// Hostname may be an IP address or hostname:
		String hostname = "drive.ecepvn.org";
		int port = 2227;

		// Connect to an SSH server:
		boolean success = ssh.Connect(hostname, port);

		// Wait a max of 5 seconds when reading responses..
		ssh.put_IdleTimeoutMs(5000);

		// Authenticate using login/password:
		success = ssh.AuthenticatePw("guest_access", "123456");
		// Once the SSH object is connected and authenticated, we use it
		// in our SCP object.
		CkScp scp = new CkScp();

		success = scp.UseSsh(ssh);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}
		String remotePath = "/volume1/ECEP/song.nguyen/DW_2020/data/sinhvien_chieu_nhom5.zip";
		String localPath = "D:\\DataWarehouse2020\\data\\source\\sinhvien_chieu_nhom5.zip";
		success = scp.DownloadFile(remotePath, localPath);
		if (success != true) {
			System.out.println(scp.lastErrorText());
			return;
		}

		System.out.println("SCP download file success.");

		// Disconnect
		ssh.Disconnect();
	}
}