package Component_1;

public class CheckFileName {
	static boolean checkFileName(String fileName, String[] syntaxs) {
		fileName = fileName.trim();
		for (String syntax : syntaxs) {
			if (fileName.startsWith(syntax)) {
				String nameGroup = fileName.substring(syntax.length());
				return checkNameGroup(nameGroup);
			}
		}
		return false;
	}

	static boolean checkNameGroup(String name) {
		String[] split = name.split("\\.");
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
		String[] syntaxs = { "sinhvien_sang_nhom", "sinhvien_chieu_nhom" };
		String fileName = "sinhvien1_sang_nhom1.xlsx";
		System.out.println(checkFileName(fileName, syntaxs));
	}
}