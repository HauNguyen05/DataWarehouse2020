package data;

public class CheckFileName {
	static boolean checkFileName(String fileName) {
		fileName = fileName.trim();
		String syntax1 = "sinhvien_sang_nhom";
		String syntax2 = "sinhvien_chieu_nhom";

		if (fileName.startsWith(syntax1)) {
			String nameGroup = fileName.substring(syntax1.length());
			return checkNameGroup(nameGroup);
		} else if (fileName.startsWith(syntax2)) {
			String nameGroup = fileName.substring(syntax2.length());
			return checkNameGroup(nameGroup);
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
		String[] type = { "xlsx", "csv", "txt", "zip", "rar" };
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

}