package com.bono.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ByteUtils {
	private static final int TRACE_LINE_BREAK_LIMIT = 64;

	public static byte[] message2byte(Serializable obj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(obj);
			return bos.toByteArray();
		}
	}

	public static Serializable byte2message(byte[] body) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(body);
		try (ObjectInput in = new ObjectInputStream(bis)) {
			return (Serializable) in.readObject();
		}
	}

	public static byte[] compress(byte[] data) {
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		deflater.finish();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length)) {
			byte[] buffer = new byte[1024];

			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				baos.write(buffer, 0, count);
			}

			return baos.toByteArray();
		} catch (IOException e) {
			// Ignore it. It's not happen.
			return null;
		}
	}

	public static byte[] decompress(byte[] data) throws DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		byte[] buffer = new byte[1024];

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length)) {
			while (!inflater.finished()) {
				try {
					int count = inflater.inflate(buffer);
					baos.write(buffer, 0, count);
				} catch (DataFormatException e) {
					throw e;
				}
			}

			return baos.toByteArray();
		} catch (IOException e) {
			// Ignore it. It's not happen.
			return null;
		}
	}

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

	public static void println(String msg) {
		System.out.println(msg);
	}

	public static void traceBytes(byte[] buf) {
		ByteUtils.traceBytes(buf, 0, buf.length);
	}

	public static void traceBytes(byte[] buf, int printLimit) {
		ByteUtils.traceBytes(buf, 0, printLimit);
	}

	public static void traceBytes(byte[] buf, int start, int printLimit) {
		StringBuilder ascIIBuilder = new StringBuilder();

		if (buf == null || start < 0 || start >= buf.length)
			return;

		int length = start + printLimit;

		if (length > buf.length)
			length = buf.length;

		println("====================================================");
		int position = 0;
		for (int i = 0; i < start % 16; i++) {
			System.out.print("   ");
			ascIIBuilder.append(' ');
			if (i == 7)
				System.out.print(" ");
		}

		for (int i = start; i < length; i++) {
			System.out.format("%02X ", buf[i]);
			position += 3;

			if (0x20 < buf[i] && buf[i] < 0x7f) {
				ascIIBuilder.append((char) buf[i]);
			} else {
				ascIIBuilder.append(".");
			}

			int offset = (i + 1) % 16;
			if (offset == 0) {
				System.out.print(" " + ascIIBuilder.toString() + "\n");
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
