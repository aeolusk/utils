package com.celizion.kcg.ems.ftp.config;

public class ConfigUtils implements ConfigKeywords {
	public static String prefix(String input, char delimeter) {
		if (input == null)
			return null;

		int idx = input.indexOf(delimeter);
		if (idx > 0)
			return input.substring(0, idx);
		return input;
	}

	public static String suffix(String input, char delimeter) {
		if (input == null)
			return null;

		int idx = input.charAt(delimeter);
		if (idx > 0)
			return input.substring(idx + 1, input.length());
		return input;
	}
}
