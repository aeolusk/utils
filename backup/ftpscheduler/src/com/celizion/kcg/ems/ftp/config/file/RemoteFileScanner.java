package com.celizion.kcg.ems.ftp.config.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.celizion.kcg.ems.ftp.config.ConfigKeywords;
import com.celizion.kcg.ems.ftp.config.InvalidConfigFormatException;
import com.celizion.kcg.ems.ftp.controller.FTPConnectionAlreadyUsedException;
import com.celizion.kcg.ems.ftp.controller.mgmt.FTPClientWrapper;
import com.celizion.kcg.ems.ftp.controller.mgmt.FTPConnectionPool;
import com.celizion.kcg.ems.ftp.model.FTPSiteInfo;
import com.celizion.kcg.ems.ftp.util.FileUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class RemoteFileScanner implements ConfigKeywords {
	private final FTPSiteInfo ftpSite;
	private final String remotePath;
	private final String pattern;

	public RemoteFileScanner(FTPSiteInfo ftpSite, String filePattern) throws InvalidConfigFormatException {
		this.ftpSite = ftpSite;
		if (filePattern.startsWith(KeywordFilePattern)) {
			String[] args = filePattern.substring(KeywordFilePattern.length(), filePattern.length()).split(",");

			if (args.length < 2)
				throw new InvalidConfigFormatException(KeywordFilePattern + " must have 2 arguments(path and pattern)");
			String directory = args[0].trim();
			if (!directory.endsWith("/") && !directory.endsWith("\\"))
				directory += File.separator;
			this.remotePath = directory;
			pattern = args[1].trim();
		} else if (filePattern.startsWith(KeywordFile)) {
			remotePath = null;
			pattern = filePattern.substring(KeywordFile.length(), filePattern.length()).trim();
		} else
			throw new InvalidConfigFormatException("expected " + KeywordFilePattern);

	}

	public List<String> scan() throws IOException, FTPConnectionAlreadyUsedException, JSchException, SftpException {
		if (remotePath == null) {
			List<String> files = new ArrayList<>();
			files.add(pattern);
			return files;
		}

		try {
			FTPClientWrapper ftpClient = FTPConnectionPool.instance.acquire(ftpSite, 100);

			List<String> remoteFiles = ftpClient.listRemoteFiles(remotePath, false);
			List<String> filterFiles = new ArrayList<>();

			for (String remoteFile : remoteFiles) {
				Pattern.matches(pattern, FileUtils.getFilename(remoteFile));
				filterFiles.add(remoteFile);
			}
			return filterFiles;
		} finally {
			FTPConnectionPool.instance.release(ftpSite);
		}
	}
}
