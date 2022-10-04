package com.bono.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

	public static List<File> unZip(File zipFile) throws FileNotFoundException, IOException {
		if (zipFile.getParentFile() != null)
			return ZipUtils.unZip(zipFile, zipFile.getParentFile());
		else
			return ZipUtils.unZip(zipFile, new File("."));
	}

	public static List<File> unZip(File zipFile, File unzipDirectory) throws FileNotFoundException, IOException {
		List<File> outputFiles = new ArrayList<>();

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile), Charset.forName("EUC-KR"))) {
			ZipEntry zipentry = null;
			while ((zipentry = zis.getNextEntry()) != null) {
				String filename = zipentry.getName();
				File unzipFile = new File(unzipDirectory.getAbsoluteFile(), filename);

				if (zipentry.isDirectory()) {
					unzipFile.mkdirs();
				} else {
					try {
						createFile(unzipFile, zis);
						outputFiles.add(unzipFile);
					} catch (Throwable e) {
						throw e;
					}
				}
			}
		}
		return outputFiles;
	}

	public static void unGZip(File zipFile, File outPath) throws FileNotFoundException, IOException {
		byte[] buf = new byte[1024 * 16];
		FileOutputStream outStream = new FileOutputStream(outPath);

		try (GZIPInputStream zis = new GZIPInputStream(new FileInputStream(zipFile))) {
			while (true) {
				int size = zis.read(buf);

				if (size <= 0)
					break;
				outStream.write(buf, 0, size);
			}
		} finally {
			outStream.close();
		}
	}

	private static void createFile(File file, ZipInputStream zis) throws FileNotFoundException, IOException {
		File parentDir = new File(file.getParent());

		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(file)) {
			byte[] buffer = new byte[102400];
			int size = 0;

			while ((size = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, size);
			}
		}
	}
}
