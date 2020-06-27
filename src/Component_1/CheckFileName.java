package Component_1;

import java.util.ArrayList;

public class CheckFileName {
	private String nameGroup;
	private String typeFile;
	private String nameFile;
	private boolean isZip;

	public ArrayList<String> information() {
		ArrayList<String> result = new ArrayList<String>();
		return result;

	}

	static boolean checkFileName(String fileName, String listSyntax) {
		String[] name = fileName.split("\\.");
		String[] syntaxs = listSyntax.split(",");
		if (name.length > 1) {
			fileName = fileName.trim();
			for (String syntax : syntaxs) {
				syntax = syntax.trim();
				if (fileName.startsWith(syntax.trim())) {
					String nameGroup = fileName.substring(syntax.length());
					return checkNameGroup(nameGroup);
				}
			}
		}
		return false;
	}

	static boolean checkNameGroup(String name) {
		String[] split = name.split("\\.");
		System.out.println("so " + split[0]);
		if (parseInt(split[0]) && typeFile(split[1])) {
			return true;
		}
		return false;
	}

	static boolean typeFile(String nameType) {
		String[] type = { "docx", "doc", "xlsx", "csv", "txt", "zip", "rar" };
		for (String string : type) {
			if (nameType.equals(string)) {
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
		String syntaxs = "sinhvien_sang_nhom, sinhvien_chieu_nhom";
		String fileName = "sinhvien_sang_nhom06.xlsx";
		System.out.println(checkFileName(fileName, syntaxs));
	}
}