package com.bono.utils;

import java.util.List;

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

	public static String suffix(String input, String delimeter) {
		if (input == null || "".equals(input))
			return input;

		int index = input.lastIndexOf(delimeter);

		if (index >= 0)
			return input.substring(index + 1, input.length());
		return input;
	}

	public static boolean isBlank(String input) {
		return input.trim().isEmpty();
	}

	public static String decapitalize(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}

		char c[] = string.toCharArray();
		c[0] = Character.toLowerCase(c[0]);

		return new String(c);
	}

	public static String list2string(List<?> values, String delimeter) {
		if (values == null || values.size() == 0)
			return "";

		if (values.size() == 1)
			return "" + values.get(0);

		StringBuilder sb = new StringBuilder();
		for (Object value : values) {
			if (sb.length() > 0)
				sb.append(delimeter);
			sb.append(value);
		}

		return sb.toString();
	}
}
