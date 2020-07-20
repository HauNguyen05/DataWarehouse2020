package component_1;

import java.util.HashMap;
import java.util.Map;

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
		String[] name = fileName.split("\\.");
		if (name.length > 1 && checkTypeFile(name[1]) && checkName(name[0], listSyntax)) {
			return true;
		} else {
			return false;
		}
	}

	boolean checkName(String fileName, String syntaxs) {
		fileName = fileName.trim();
		syntaxs = syntaxs.trim();
		String[] textSyntax = syntaxs.split("_");
		String[] arrName = fileName.split("_");
		boolean checkGroup = false;
		boolean checkSeq = false;
		boolean checkCa = false;
		if (arrName.length == textSyntax.length) {
			for (int i = 0; i < arrName.length; i++) {
				if (i == 1) {
					if (arrName[i].equals("sang") || arrName[i].equals("chieu")) {
						checkCa = true;
					}
				} else if (i == 2 && arrName[i].contains("nhom")) {
					String group = (String) arrName[i].subSequence(4, arrName[i].length());
					if (!parseInt(group)) {
						return false;
					} else {
						checkGroup = true;
					}
				} else {
					if (!arrName[i].equals(textSyntax[i])) {
						checkSeq = false;
						return false;
					} else {
						checkSeq = true;
					}
				}
			}
			if (checkGroup && checkSeq && checkCa) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
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
					if (nameType.equals("xlsx")) {
						this.ignore = "0";
					} else {
						this.ignore = "1";
					}
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
		boolean a = c.checkFileName("monhoc_chieu_nhom5_2020.zip", "monhoc_ca_nhom_2020");
		System.out.println(a);
	}
}