package com.celizion.kcg.ems.ftp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.celizion.kcg.ems.ftp.controller.mgmt.FTPClientWrapper;
import com.celizion.kcg.ems.ftp.controller.mgmt.FTPConnectionPool;
import com.celizion.kcg.ems.ftp.model.FTPSiteInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FTPThreadPoolExecutor {
	instance;

	private static final int MAXIMUM_FTP_CONNECTION = 500;
	private static final int MAXIMUM_POOL_SIZE = 10;

	private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(MAXIMUM_FTP_CONNECTION);
	private ThreadPoolExecutor executorService = new ThreadPoolExecutor(1, MAXIMUM_POOL_SIZE, 10, TimeUnit.SECONDS,
			queue);

	private Map<FTPSiteInfo, Queue<Runnable>> ftpJobsMap = new HashMap<>();

	public synchronized void clear() {
		if (executorService != null && !executorService.isShutdown()) {
			executorService.shutdown();
			executorService = null;
		}
	}

	private synchronized void addFTPJob(FTPSiteInfo siteInfo, Runnable runnable) {
		Queue<Runnable> jobList = ftpJobsMap.get(siteInfo);
		if (jobList == null) {
			jobList = new LinkedBlockingQueue<>();
			ftpJobsMap.put(siteInfo, jobList);
		}
		jobList.add(runnable);
	}

	private synchronized Runnable getNextFTPJob(FTPSiteInfo siteInfo) {
		Queue<Runnable> jobList = ftpJobsMap.get(siteInfo);
		if (jobList != null && jobList.size() > 0) {
			Runnable next = jobList.poll();
			log.debug(siteInfo + " :: queue size = " + jobList.size());
			if (next == null)
				ftpJobsMap.remove(siteInfo);
			return next;
		}
		return null;
	}

	private List<FTPSiteInfo> onWorkingList = new ArrayList<>();

	private void executeFTPJobs(FTPSiteInfo siteInfo) {
		synchronized (onWorkingList) {
			if (!onWorkingList.contains(siteInfo)) {
				onWorkingList.add(siteInfo);
				executorService.execute(() -> {
					while (true) {
						Runnable ftpJob = null;
						synchronized (onWorkingList) {
							ftpJob = getNextFTPJob(siteInfo);
							if (ftpJob == null) {
								onWorkingList.remove(siteInfo);
								break;
							}
						}
						try {
							ftpJob.run();
						} catch (Exception e) {
							log.error("{}", e);
						}
					}
				});
				log.debug("Create FTP worker for " + siteInfo + ". (poolsize= " + executorService.getPoolSize() + ")");
			}
		}
	}

	public void uploadFile(FTPSiteInfo siteInfo, File localFile, String remotePath,
			BiConsumer<Boolean, Object[]> resultHandler) {
		try {
			addFTPJob(siteInfo, () -> {
				try {
					log.debug("Try to upload host=" + siteInfo + ", localFile=" + localFile.getAbsolutePath()
							+ ", remotePath=" + remotePath);
					FTPClientWrapper ftpClient = FTPConnectionPool.instance.acquire(siteInfo, 10);

					ftpClient.uploadFile(localFile, remotePath);
					if (resultHandler != null)
						resultHandler.accept(true, buildArgs(siteInfo, localFile, remotePath));
				} catch (Exception e) {
					log.error("{}", e);
					if (resultHandler != null)
						resultHandler.accept(false, buildArgs(siteInfo, localFile, remotePath));
				} finally {
					FTPConnectionPool.instance.release(siteInfo);
				}
			});
			executeFTPJobs(siteInfo);
		} catch (Exception e) {
			log.error("{}", e);
		}
	}

	public void downloadFile(FTPSiteInfo siteInfo, String remoteFilePath, File localFile,
			BiConsumer<Boolean, Object[]> resultHandler) {
		try {
			addFTPJob(siteInfo, () -> {
				try {
					log.debug("Try to download host=" + siteInfo + ", remoteFilePath=" + remoteFilePath + ", localFile="
							+ localFile);
					FTPClientWrapper ftpClient = FTPConnectionPool.instance.acquire(siteInfo, 10);

					ftpClient.downloadFile(remoteFilePath, localFile);
					if (resultHandler != null)
						resultHandler.accept(true, buildArgs(siteInfo, remoteFilePath, localFile));
				} catch (Exception e) {
					log.error("{}", e);
					if (resultHandler != null)
						resultHandler.accept(false, buildArgs(siteInfo, remoteFilePath, localFile));
				} finally {
					FTPConnectionPool.instance.release(siteInfo);
				}
			});
			executeFTPJobs(siteInfo);
		} catch (Exception e) {
			log.error("{}", e);
		}
	}

	private Object[] buildArgs(Object... args) {
		Object[] results = new Object[args.length];

		for (int i = 0; i < args.length; i++)
			results[i] = args[i];
		return results;
	}
}
