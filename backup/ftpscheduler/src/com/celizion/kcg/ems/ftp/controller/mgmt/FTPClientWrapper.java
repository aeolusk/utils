package com.celizion.kcg.ems.ftp.controller.mgmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.celizion.kcg.ems.ftp.util.FileUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FTPClientWrapper {
	// for FTP
	private FTPClient ftpClient = null;

	// for SFTP
	private JSch jsch = null;
	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	public FTPClientWrapper() {
		// do nothing.
	}

	public void connect(String host, String user, String password) throws IOException, JSchException {
		this.connect(host, 0, false, user, password);
	}

	public void connect(String host, int port, boolean enableSFTP, String user, String password)
			throws IOException, JSchException {
		if (!enableSFTP)
			ftpClient = new FTPClient();
		else
			jsch = new JSch();

//		ftpClient.setControlEncoding("UTF-8");
		if (ftpClient != null)
			initFTPClient(host, port, user, password);

		if (jsch != null)
			initSFTPClient(host, port, user, password);

	}

	private void initFTPClient(String host, int port, String user, String password)
			throws SocketException, IOException {
		int replyCode;
		ftpClient.setControlEncoding("EUC-KR");

		ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		if (port > 0)
			ftpClient.connect(host, port);
		else
			ftpClient.connect(host);

		replyCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			ftpClient.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}

		// ftpClient.setSoTimeout(10000);
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//		ftpClient.enterLocalPassiveMode();
		ftpClient.login(user, password);
	}

	private void initSFTPClient(String host, int port, String user, String password)
			throws SocketException, IOException, JSchException {
		session = jsch.getSession(user, host, port);
		session.setPassword(password);

		java.util.Properties config = new java.util.Properties();

		config.put("StrictHostKeyChecking", "no");

		session.setConfig(config);
		session.connect();

		channel = session.openChannel("sftp");
		channel.connect();

		channelSftp = (ChannelSftp) channel;
	}

	public boolean isConnected() {
		if (ftpClient != null && ftpClient.isConnected())
			return true;

		if (channelSftp != null && channelSftp.isConnected())
			return true;
		return false;
	}

	public void clear() throws IOException {
		if (ftpClient != null && ftpClient.isConnected())
			ftpClient.disconnect();
		if (channelSftp != null && channelSftp.isConnected()) {
			channelSftp.exit();
			channel.disconnect();
			session.disconnect();
		}
		channelSftp = null;
		channel = null;
		session = null;
		ftpClient = null;
	}

	public void clearSilent() {
		try {
			clear();
		} catch (IOException e) {
			// do nothing.
		}
	}

	public void uploadFile(File localFile, String remotePath) throws Exception {
		try (InputStream input = new FileInputStream(localFile)) {
			if (ftpClient != null) {
				if (this.ftpClient.storeFile(remotePath, input)) {
					log.debug("Upload success. File " + localFile.getAbsolutePath() + " to " + remotePath);
				} else {
					log.warn("Upload fail. File " + localFile.getAbsolutePath() + " to " + remotePath);
				}
			} else if (channelSftp != null) {
				channelSftp.cd(FileUtils.getDirectory(remotePath));
				channelSftp.put(input, FileUtils.getFilename(remotePath));
			}
		}
	}

	public void downloadFile(String remoteFilePath, File localFile) throws Exception {
		FileUtils.makeDirectory((localFile.getParentFile()));

		try (FileOutputStream outStream = new FileOutputStream(localFile)) {
			if (ftpClient != null) {
				if (ftpClient.retrieveFile(remoteFilePath, outStream)) {
					log.debug("Download success. File " + remoteFilePath + " to " + localFile.getAbsolutePath());
				} else {
					log.warn("Upload fail. File " + remoteFilePath + " to " + localFile.getAbsolutePath());
				}
			} else if (channelSftp != null) {
				InputStream in = null;
				OutputStream out = null;

				channelSftp.cd(FileUtils.getDirectory(remoteFilePath));
				in = channelSftp.get(FileUtils.getFilename(remoteFilePath));

				try {
					byte[] buf = new byte[10240];
					out = new FileOutputStream(localFile);
					int read;
					while ((read = in.read(buf)) > 0) {
						out.write(buf, 0, read);
					}
				} catch (IOException e) {
					log.error("{}", e);
				} finally {
					try {
						out.close();
						in.close();
					} catch (IOException e) {
						// do nothing.
					}
				}
			}
		}
	}

	public void makeRemoteDirectory(String remoteDirectory) throws IOException, SftpException {
		if (ftpClient != null && ftpClient.changeWorkingDirectory(remoteDirectory))
			return;

		if (channelSftp != null) {
			try {
				channelSftp.cd(remoteDirectory);
				return; // exist directory.
			} catch (SftpException e) {
				// directory isn't exist.
			}
		}

		String parentDirectory = FileUtils.getParentDirectory(remoteDirectory);

		if (parentDirectory != null)
			makeRemoteDirectory(parentDirectory);

		if (ftpClient != null)
			ftpClient.makeDirectory(remoteDirectory);

		if (channelSftp != null)
			channelSftp.mkdir(parentDirectory);
	}

	public List<String> listRemoteFiles(String _remotePath, boolean traceHierarchy) throws IOException, SftpException {
		List<String> files = new ArrayList<>();
		String remotePath = FileUtils.endWithDelimeter(_remotePath);

		if (ftpClient != null) {
			if (ftpClient.changeWorkingDirectory(remotePath)) {
				for (FTPFile file : ftpClient.listFiles()) {
					if (file.isFile()) {
						files.add(remotePath + file.getName());
					} else if (traceHierarchy && file.isDirectory()) {
						files.addAll(listRemoteFiles(remotePath + file.getName() + File.separator, traceHierarchy));
					}
				}
			}
		} else if (channelSftp != null) {
			channelSftp.cd(remotePath);
			@SuppressWarnings("unchecked")
			Collection<LsEntry> fileList = (Collection<LsEntry>) channelSftp.ls(remotePath);
			if (fileList != null)
				for (LsEntry entry : fileList) {
					if (entry.getAttrs().isReg()) {
						files.add(remotePath + entry.getFilename());
					} else if (traceHierarchy && entry.getAttrs().isDir()) {
						files.addAll(
								listRemoteFiles(remotePath + entry.getFilename() + File.separator, traceHierarchy));
					}
				}
		}
		return files;
	}

	public void logout() throws IOException {
		if (ftpClient != null && ftpClient.isConnected()) {
			try {
				this.ftpClient.logout();
			} catch (IOException e) {
				log.error("{}", e);
			}
		}
		if (channelSftp != null && channelSftp.isConnected()) {
			channelSftp.exit();
		}
	}
}
