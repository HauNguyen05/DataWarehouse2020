package component_1;

import java.util.HashMap;
import java.util.Map;

public class CheckFileName {
	private String typeFile;
	private String isUnzip;
	private String ignore;
	
	// lấy thông tin của file lưu vào map
	public Map<String, String> information() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("typeFile", this.typeFile);
		result.put("isUnzip", this.isUnzip);
		result.put("ignore", this.ignore);
		return result;

	}
	// kiểm tra tên file, nhận vào fileName và list các syntax
	boolean checkFileName(String fileName, String listSyntax) {
		String[] a = listSyntax.split(",");
		for (String syntax : a) {
			// bỏ khoảng trắng trước syntax
			syntax = syntax.trim();
			//kiểm tra tên file có đúng syntax không
			if (fileName.matches(syntax)) {
				// chia tên file thành 2 phần theo dấu .
				String[] name = fileName.split("\\.");
				this.typeFile = name[1];
				switch (name[1]) {
				case "zip":
					this.isUnzip = "1";
					break;
				case "rar":
					this.isUnzip = "1";
					break;
				case "txt":
					this.isUnzip = "0";
					this.ignore = "1";
					break;
				case "csv":
					this.isUnzip = "0";
					this.ignore = "1";
					break;
				case "xlsx":
					this.isUnzip = "0";
					this.ignore = "0";
					break;
				default:
					return false;
				}
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		CheckFileName c = new CheckFileName();
		boolean a = c.checkFileName("Monhoc2013.csv","Monhoc(2013|2014).csv");
		System.out.println(a);
	}
}