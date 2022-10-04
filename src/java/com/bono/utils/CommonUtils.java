package com.bono.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonUtils {
	public static String exception2String(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		return sw.toString();
	}

	private static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String timeMilliesToString(long timeMillies) {
		return timeMilliesToString(defaultDateFormat, timeMillies);
	}

	private static SimpleDateFormat defaultDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static String timeMilliesToString2(long timeMillies) {
		return timeMilliesToString(defaultDateFormat2, timeMillies);
	}
	
	private static SimpleDateFormat ctrFileFolderFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

	public static String timeMilliesToFolderName() {
		return timeMilliesToString(ctrFileFolderFormat, System.currentTimeMillis());
	}
	public static String timeMilliesToFolderName(long timeMillies) {
		return timeMilliesToString(ctrFileFolderFormat, timeMillies);
	}
	
	// 20220310.1715+0900-1730+0900
	private static SimpleDateFormat ctrFileDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmZ");

	public static String timeMillies2CtrFileDate(long timeMillies) {
		return timeMilliesToString(ctrFileDateFormat, timeMillies);
	}
	
	public static long getStartTimeOfEvery10Minute(long millies) {
		Calendar calc = Calendar.getInstance();
		calc.setTimeInMillis(millies);
		
		int minute = calc.get(Calendar.MINUTE);
		calc.set(Calendar.MINUTE, minute / 10 * 10);
		calc.set(Calendar.SECOND, 0);
		calc.set(Calendar.MILLISECOND, 0);
		return calc.getTimeInMillis();
	}

	public static Date ctrFileDateString2Date(String timestamp) throws ParseException {
		return ctrFileDateFormat.parse(timestamp);
	}

	public static String timeMilliesToString(SimpleDateFormat dateFmt, long timeMillies) {
		return dateFmt.format(new Date(timeMillies));
	}

	public static Calendar localDateTimeToDate(LocalDateTime localDateTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
				localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
		return calendar;
	}

	public static Object[] buildArgs(Object... args) {
		Object[] results = new Object[args.length];

		for (int i = 0; i < args.length; i++)
			results[i] = args[i];
		return results;
	}

	public static <T> List<T> buildList(@SuppressWarnings("unchecked") T... args) {
		List<T> results = new ArrayList<>();

		for (int i = 0; i < args.length; i++)
			results.add(args[i]);
		return results;
	}
}
