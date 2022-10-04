package com.bono.utils;

import java.nio.ByteBuffer;

public class ByteUtils {
	public static void println(String msg) {
		System.out.println(msg);
	}

	public static void traceAsBytes(int[] intAry) {
		ByteBuffer buf = ByteBuffer.allocate(intAry.length * 4);
		for (int i = 0; i < intAry.length; i++) {
			buf.putInt(intAry[i]);
		}
		ByteUtils.traceBytes(buf.array());
	}

	public static void traceBytes(byte[] buf) {
		ByteUtils.traceBytes(null, buf);
	}

	public static void traceBytes(String title, byte[] buf) {
		ByteUtils.traceBytes(title, buf, 0, buf.length);
	}

	public static void traceBytes(byte[] buf, int printLimit) {
		ByteUtils.traceBytes(buf, 0, printLimit);
	}

	public static void traceBytes(String title, byte[] buf, int printLimit) {
		ByteUtils.traceBytes(title, buf, 0, printLimit);
	}

	public static void traceBytes(byte[] buf, int start, int printLimit) {
		ByteUtils.traceBytes(null, buf, start, printLimit);
	}

	public static void traceBytes(String title, byte[] buf, int start, int printLimit) {
		StringBuilder ascIIBuilder = new StringBuilder();

		if (buf == null || start < 0 || start >= buf.length)
			return;

		int length = start + printLimit;

		if (length > buf.length)
			length = buf.length;

		if (title != null && !"".equals(title)) {
			println("====================================================");
			println("[" + title + "]");
		} else
			println("====================================================");
		int position = 0;
		for (int i = 0; i < start % 16; i++) {
			System.out.print("   ");
			ascIIBuilder.append(' ');
			if (i == 7)
				System.out.print(" ");
		}

		boolean newLine = false;
		for (int i = start; i < length; i++) {
			if (newLine) {
				System.out.println();
				newLine = false;
			}
			System.out.format("%02X ", buf[i]);
			position += 3;
			int offset = (i + 1) % 16;

			if (0x20 < buf[i] && buf[i] < 0x7f) {
				ascIIBuilder.append((char) buf[i]);
			} else {
				ascIIBuilder.append(".");
			}

			if (offset == 0) {
				System.out.print(" " + ascIIBuilder.toString());
				newLine = true;
				position = 0;
				ascIIBuilder = new StringBuilder();
			} else if (offset == 8) {
				System.out.print(" ");
				position += 1;
			}
		}

		for (int i = 0; i < start % 16; i++) {
			System.out.print("   ");
			ascIIBuilder.append(' ');
			if (i == 7)
				System.out.print(" ");
		}
		for (int i = position; i < 49; i++)
			System.out.print(" ");
		System.out.print(" " + ascIIBuilder.toString());
		println("\n====================================================");
	}

	public static String byte2String(byte[] buf) {
		return byte2String(new StringBuilder(), buf);
	}

	public static String byte2String(int totalLength, byte[] buf) {
		StringBuilder sb = new StringBuilder();
		if (totalLength > buf.length)
			for (int i = 0; i < (totalLength - buf.length); i++) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append("00");
			}

		return byte2String(sb, buf);
	}

	private static String byte2String(StringBuilder sb, byte[] buf) {
		for (int i = 0; i < buf.length; i++) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(String.format("%02X", buf[i]));
		}
		return sb.toString();
	}

}
