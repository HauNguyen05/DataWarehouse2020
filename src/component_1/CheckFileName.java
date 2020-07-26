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
				default:
					break;
				}
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		CheckFileName c = new CheckFileName();
		boolean a = c.checkFileName("monhoc_chieu_nhom5_2020.csv",
				"monhoc_(sang|chieu)_nhom([0-9]|[0-9][0-9])_2020.csv, monhoc_(sang|chieu)_nhom[0-16]_2020.txt, monhoc_(sang|chieu)_nhom[0-16]_2020.zip");
		System.out.println(a);
	}
}