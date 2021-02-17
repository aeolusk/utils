package com.celizion.kcg.ems.tta.simulator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CybertelPODParser {
	public static PODData parseAlarmPOD(String podFormat) {
		String alarmPOD = FMSimulator.getCybertelAlarmPOD();

		String[] lines = alarmPOD.split("\n");
		parseFirstLine(lines[0]);
		parseSecondLine(lines[1]);

		for (int i = 2; i < lines.length; i++) {
			if ("COMPLETED".equals(lines[i].trim()))
				break;

			int delimeter = lines[i].indexOf("=");
			if (delimeter > 0) {
				System.out.println("key=" + lines[i].substring(0, delimeter) + ", value="
						+ lines[i].substring(delimeter + 1, lines[i].length()));
			}
		}
		return null;
	}

	private static String[] parseFirstLine(String input) {
		Pattern pattern = Pattern.compile("([A-Z0-9_]+) ([0-9-]+ [0-9:]+.\\d+)"); // 영문자만

		Matcher matcher = pattern.matcher(input.trim());

		if (matcher.find()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
		}
		return null;
	}

	private static String[] parseSecondLine(String input) {
		Pattern pattern = Pattern.compile("([*#]+) ([A-Z]\\d+) ([A-Z ]+)"); // 영문자만

		Matcher matcher = pattern.matcher(input.trim());

		if (matcher.find()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
		}
		return null;
	}
}
