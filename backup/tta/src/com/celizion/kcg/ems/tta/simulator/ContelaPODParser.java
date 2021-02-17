package com.celizion.kcg.ems.tta.simulator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.celizion.kcg.ems.tta.util.StringUtils;

public class ContelaPODParser {
	public static PODData parseAlarmPOD(String podFormat) {
		String alarmPOD = FMSimulator.getContelaAlarmPOD();

		String[] lines = alarmPOD.split("\n");
		parseFirstLine(lines[0]);

		for (int i = 1; i < lines.length; i++) {
			String input = lines[i].trim();
			if ("COMPLETED".equals(input))
				break;

			String prefix = StringUtils.prefix(input, " ");
			String key = "";
			String value = "";

			switch (prefix) {
			case "LOCATION":
				key = "LOCATION";
				value = input.substring("LOCATION".length(), input.length()).trim();
				break;
			case "GRADE":
				key = "GRAGE";
				value = input.substring("GRADE".length(), input.length()).trim();
				break;
			case "EVENTTYPE":
				key = "EVENTTYPE";
				value = input.substring("EVENTTYPE".length(), input.length()).trim();
				break;
			case "PROBABLECAUSE":
				key = "PROBABLECAUSE";
				value = input.substring("PROBABLECAUSE".length(), input.length()).trim();
				break;
			case "ADDITIONAL":
				key = "ADDITIONAL INFO";
				value = input.substring("ADDITIONAL INFO".length(), input.length()).trim();
				break;
			case "OCCURRED":
				key = "OCCURRED TIME";
				value = input.substring("OCCURRED TIME".length(), input.length()).trim();
				break;
			}
			System.out.println("key=" + key + ", value=" + value);
		}
		return null;
	}

	private static String[] parseFirstLine(String input) {
		Pattern pattern = Pattern.compile("([*#]+):(\\w+) ([A-Z]\\d+) ([A-Z_]+)");

		Matcher matcher = pattern.matcher(input.trim());

		if (matcher.find()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
		}
		return null;
	}
}
