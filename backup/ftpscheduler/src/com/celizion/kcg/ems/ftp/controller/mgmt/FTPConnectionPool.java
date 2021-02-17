package com.celizion.kcg.ems.ftp.controller.mgmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.celizion.kcg.ems.ftp.controller.FTPConnectionAlreadyUsedException;
import com.celizion.kcg.ems.ftp.controller.LoginInfoManager;
import com.celizion.kcg.ems.ftp.model.FTPSiteInfo;
import com.celizion.kcg.ems.ftp.model.LoginInfo;
import com.jcraft.jsch.JSchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FTPConnectionPool {
	instance;

	private Map<FTPSiteInfo, FTPClientWrapper> connectionPool = new ConcurrentHashMap<>();
	private Map<FTPSiteInfo, FTPClientWrapper> usedPool = new ConcurrentHashMap<>();
	private Map<FTPSiteInfo, Long> lastUsedTimeInfo = new ConcurrentHashMap<>();

	private Timer timer;

	public synchronized void startUnusedFTPClientRemover() {
		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					removeUnusedConnection();
				} catch (Exception e) {
					// do nothing.
				}
			}
		}, 60000, 60000);

	}

	public synchronized FTPClientWrapper connect(FTPSiteInfo host) throws IOException, JSchException {
		FTPClientWrapper ftpClient = connectionPool.get(host);

		if (ftpClient != null && ftpClient.isConnected()) {
			log.debug("Used already connected ftp client (" + host + ")");
			return ftpClient;
		}

		LoginInfo loginInf = LoginInfoManager.instance.get(host.hostname);

		if (loginInf == null) {
			log.warn("Can't fine login information to " + host);
			throw new IOException("Can't find login information.");
		}

		if (ftpClient != null)
			ftpClient.clear();

		ftpClient = new FTPClientWrapper();

		ftpClient.connect(host.hostname, host.port, host.enableSFTP, loginInf.user, loginInf.password);
		connectionPool.put(host, ftpClient);
		return ftpClient;
	}

	public synchronized FTPClientWrapper acquire(FTPSiteInfo host, int maxWaitingSeconds)
			throws IOException, FTPConnectionAlreadyUsedException, JSchException {
		int waitingTime = 0;
		while (true) {
			if (waitingTime >= maxWaitingSeconds * 1000)
				throw new FTPConnectionAlreadyUsedException(
						"FTP connection pool acquire timeout " + maxWaitingSeconds + " second(s)");
			try {
				return acquire(host);
			} catch (FTPConnectionAlreadyUsedException e) {
				try {
					waitingTime += 1000;
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// do nothing.
				}
			} catch (IOException ioe) {
				throw ioe;
			}
		}
	}

	public synchronized FTPClientWrapper acquire(FTPSiteInfo host)
			throws IOException, FTPConnectionAlreadyUsedException, JSchException {
		checkUsedPool(host);
		FTPClientWrapper connectedClient = connect(host);

		usedPool.put(host, connectedClient);
		return connectedClient;
	}

	private void checkUsedPool(FTPSiteInfo host) throws FTPConnectionAlreadyUsedException {
		FTPClientWrapper ftpClient = usedPool.get(host);
		if (ftpClient != null) {
			if (!ftpClient.isConnected()) {
				connectionPool.remove(host);
				usedPool.remove(host);

				ftpClient.clearSilent();
				return;
			}

			throw new FTPConnectionAlreadyUsedException("");
		}
	}

	public synchronized void release(FTPSiteInfo host) {
		usedPool.remove(host);
		lastUsedTimeInfo.put(host, System.currentTimeMillis());
	}

	private final int MAX_PRESERVE_TIME = 60 * 1000; // 60 seconds.

	public synchronized void removeUnusedConnection() {
		long baseTime = System.currentTimeMillis() - MAX_PRESERVE_TIME;
		List<FTPSiteInfo> removeList = new ArrayList<>();

		for (Entry<FTPSiteInfo, FTPClientWrapper> entry : connectionPool.entrySet()) {
			Long lastUsedTime = lastUsedTimeInfo.get(entry.getKey());

			if (lastUsedTime != null && lastUsedTime < baseTime) {
				if (!usedPool.containsKey(entry.getKey()))
					removeList.add(entry.getKey());
			}
		}

		for (FTPSiteInfo siteInfo : removeList) {
			FTPClientWrapper ftpClient = connectionPool.remove(siteInfo);
			usedPool.remove(siteInfo);
			lastUsedTimeInfo.remove(siteInfo);

			if (ftpClient != null) {
				log.debug("clear ftp client [" + siteInfo + "]");
				ftpClient.clearSilent();
			}
		}
	}
}
