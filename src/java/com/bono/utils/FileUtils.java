package com.bono.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtils {
	public static String endWithDelimeter(String directory) {
		if (directory == null || directory.length() == 0)
			return directory;

		char lastChar = directory.charAt(directory.length() - 1);
		if (lastChar == '\\' || lastChar == '/')
			return directory;

		return directory + "/";
	}

	public static String getUnixPath(String path) {
		if (path == null)
			return null;

		return path.replace('\\', '/');
	}

	public static String getPath(String filePath) {
		return getDirectory(filePath);
	}

	public static String getDirectory(String filePath) {
		if (filePath == null)
			return null;

		int delimeter = filePath.lastIndexOf("/");

		if (delimeter == -1)
			delimeter = filePath.lastIndexOf("\\");

		if (delimeter > 0)
			return filePath.substring(0, delimeter + 1);

		return filePath;
	}

	public static String getFilename(String filePath) {
		if (filePath == null || filePath.length() == 0)
			return null;

		int delimeter = filePath.lastIndexOf("/");

		if (delimeter == -1)
			delimeter = filePath.lastIndexOf("\\");

		if (delimeter > 0)
			return filePath.substring(delimeter + 1, filePath.length());

		return filePath;
	}

	public static File getParentIfExist(File file) {
		if (file.getParentFile() == null)
			return file;
		return file.getParentFile();
	}

	public static String getParentDirectory(String directoryPath) {
		String findPath = directoryPath;

		if (findPath == null || findPath.length() == 0)
			return null;

		char lastChar = findPath.charAt(findPath.length() - 1);
		if (lastChar == '/' || lastChar == '\\')
			findPath = findPath.substring(0, findPath.length() - 1);

		int delimeter = findPath.lastIndexOf("/");

		if (delimeter == -1)
			delimeter = findPath.lastIndexOf("\\");

		if (delimeter > 0)
			return findPath.substring(0, delimeter);

		return null;
	}

	public static boolean makeDirectory(String directoryPath) {
		return makeDirectory(new File(directoryPath));
	}

	public static boolean makeDirectory(File directoryPath) {
		if (directoryPath == null)
			return true;

		if (directoryPath.exists()) {
			if (directoryPath.isDirectory())
				return true;
			return false;
		}

		makeDirectory(directoryPath.getParentFile());
		return directoryPath.mkdir();
	}

	public static List<File> listFiles(File path, boolean traceHierarchy) throws IOException {
		return listFiles(path, null, traceHierarchy);
	}

	public static List<File> listFiles(File path, String pattern, boolean traceHierarchy) throws IOException {
		List<File> files = new ArrayList<>();

		if (path == null || !path.isDirectory())
			throw new IOException(path + " isn't directory(or isn't exist).");

		for (File file : path.listFiles()) {
			if (file.isFile()) {
				if (pattern == null || "".equals(pattern) || Pattern.matches(pattern, file.getName()))
					files.add(file);
			} else if (traceHierarchy && file.isDirectory()) {
				files.addAll(listFiles(file, pattern, traceHierarchy));
			}
		}
		return files;
	}
}
