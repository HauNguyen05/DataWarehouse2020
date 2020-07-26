package component_1;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckFileName {
	private String typeFile;
	private String isUnzip;
	private String ignore;

	public Map<String, String> information() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("typeFile", this.typeFile);
		result.put("isUnzip", this.isUnzip);
		result.put("ignore", this.ignore);
		return result;

	}

	boolean checkFileName(String fileName, String listSyntax) {
		String[] a = listSyntax.split(",");
		for (String syntax : a) {
			syntax = syntax.trim();
			if (fileName.matches(syntax)) {
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
//		System.out.println("sinhvien_sang_nhom10.xlsx".matches("sinhvien_(sang|chieu)_nhom([0-9]|[0-9][0-9]).xlsx"));
		CheckFileName c = new CheckFileName();
		boolean a = c.checkFileName("sinhvien_sang_nhom0.xlsx","sinhvien_(sang|chieu)_nhom([0-9]|[0-9][0-9]).xlsx");
		System.out.println(a);
	}
}