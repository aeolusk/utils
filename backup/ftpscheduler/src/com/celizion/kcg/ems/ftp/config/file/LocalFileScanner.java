package com.celizion.kcg.ems.ftp.config.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.celizion.kcg.ems.ftp.config.ConfigKeywords;
import com.celizion.kcg.ems.ftp.config.InvalidConfigFormatException;
import com.celizion.kcg.ems.ftp.util.FileUtils;

public class LocalFileScanner implements ConfigKeywords {
	private final File directory;
	private final String pattern;

	public LocalFileScanner(String filePattern) throws InvalidConfigFormatException {
		if (filePattern.startsWith(KeywordFilePattern)) {
			String[] args = filePattern.substring(KeywordFilePattern.length(), filePattern.length()).split(",");

			if (args.length < 2)
				throw new InvalidConfigFormatException(KeywordFilePattern + " must have 2 arguments(path and pattern)");
			directory = new File(args[0].trim());
			pattern = args[1].trim();
		} else if (filePattern.startsWith(KeywordFile)) {
			directory = null;
			pattern = filePattern.substring(KeywordFile.length(), filePattern.length()).trim();
		} else
			throw new InvalidConfigFormatException("expected " + KeywordFilePattern);

	}

	public List<File> scan() throws IOException {
		if (directory == null) {
			List<File> files = new ArrayList<>();
			files.add(new File(pattern));
			return files;
		}

		return FileUtils.listFiles(directory, pattern, false);
	}
}
