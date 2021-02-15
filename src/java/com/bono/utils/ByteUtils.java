package com.bono.utils;

public class ByteUtils {
	private static final int TRACE_LINE_BREAK_LIMIT = 64;

	public static void trace(byte[] data) {
		if (data == null)
			System.out.println("Empty data.");

		StringBuilder sb = new StringBuilder();
		sb.append("==========================================================\n");
		for (int i = 0; i < data.length; i++) {
			if (i != 0 && i % TRACE_LINE_BREAK_LIMIT == 0)
				sb.append("\n");
			if (i % 8 == 0)
				sb.append("  ");

			sb.append(String.format("%02X ", data[i]));
		}
		sb.append("\n==========================================================\n");
		System.out.println(sb.toString());
	}

	public static void traceSimple(byte[] data) {
		if (data == null)
			System.out.println("Empty data.");

		StringBuilder sb = new StringBuilder();
		sb.append("==========================================================\n");
		int lineCount = data.length / TRACE_LINE_BREAK_LIMIT + ((data.length % TRACE_LINE_BREAK_LIMIT > 0) ? 1 : 0);
		for (int i = 0; i < data.length; i++) {
			int lineNumber = i / TRACE_LINE_BREAK_LIMIT + 1;
			if (lineNumber == 1 || lineNumber == lineCount) {
				sb.append(String.format("%02X ", data[i]));
			} else if (lineCount > 2 && i == TRACE_LINE_BREAK_LIMIT) {
				sb.append("\n         .. .. .. .. .. ..\n");
			} else if (lineCount == 2 && i == TRACE_LINE_BREAK_LIMIT) {
				sb.append("\n");
			}
		}
		sb.append("\n==========================================================\n");
		System.out.println(sb.toString());
	}
}
