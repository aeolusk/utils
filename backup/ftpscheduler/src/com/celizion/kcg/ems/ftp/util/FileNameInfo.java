package com.celizion.kcg.ems.ftp.util;

public class FileNameInfo {
	public final String path;
	public final String name;

	private FileNameInfo(String path, String name) {
		this.path = path;
		this.name = name;
	}

	public static FileNameInfo create(String filepath) {
		if (filepath != null && filepath.length() > 0) {
			int delimeter = filepath.lastIndexOf("/");

			if (delimeter == -1)
				delimeter = filepath.lastIndexOf("\\");

			if (delimeter > 0) {
				return new FileNameInfo(filepath.substring(0, delimeter + 1),
						filepath.substring(delimeter + 1, filepath.length()));
			}
		}
		return new FileNameInfo("", "");
	}
}
