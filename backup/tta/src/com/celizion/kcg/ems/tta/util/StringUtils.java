package com.celizion.kcg.ems.tta.util;

public class StringUtils {
	public static String toSingleLine(String original) {
		if (original == null)
			return original;
		return original.replace("\n", "\\n");
	}

	public static String abbreviate(int prefixLength, int suffixLength, String original) {
		if (original == null)
			return original;
		int length = original.length();
		if (length > prefixLength + suffixLength + 5) {
			return original.substring(0, prefixLength) + " ... " + original.substring(length - suffixLength, length);
		}
		return original;
	}

	public static String prefix(String input, String delimeter) {
		if (input == null || "".equals(input))
			return input;

		int index = input.indexOf(delimeter);

		if (index >= 0)
			return input.substring(0, index);
		return input;
	}
}
