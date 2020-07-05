package component_1;


import java.util.HashMap;
import java.util.Map;

public class CheckFileName {
	private String typeFile;
	private String isUnzip;

	public Map<String, String> information() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("typeFile", this.typeFile);
		result.put("isUnzip", this.isUnzip);
		return result;

	}

	boolean checkFileName(String fileName, String listSyntax) {
		String[] name = fileName.split("\\.");
		String[] syntaxs = listSyntax.split(",");
		boolean result = false;
		if (name.length > 1) {
			fileName = fileName.trim();
			for (String syntax : syntaxs) {
				syntax = syntax.trim();
				if (fileName.startsWith(syntax.trim())) {
					String nameGroup = fileName.substring(syntax.length());
					result = checkNameGroup(nameGroup);
					if (result) {
						information();
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean checkNameGroup(String name) {
		String[] split = name.split("\\.");
		if (parseInt(split[0]) && checkTypeFile(split[1])) {
			return true;
		}
		return false;
	}

	public boolean checkTypeFile(String nameType) {
		String[] type = { "docx", "doc", "xlsx", "csv", "txt", "zip", "rar" };
		for (String string : type) {
			if (nameType.equals(string)) {
				this.typeFile = string;
				if (nameType.equals("zip") || nameType.equals("rar")) {
					this.isUnzip = "1";
				} else {
					this.isUnzip = "0";
				}
				return true;
			}
		}
		return false;
	}

	static boolean parseInt(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public static void main(String[] args) {
		CheckFileName c = new CheckFileName();
		boolean a =c.checkFileName("sinhvien_chieu_nhom11.xlsx", "sinhvien_sang_nhom, sinhvien_chieu_nhom");
		System.out.println(a);
		System.out.println(c.information().get("typeFile"));
	}
}