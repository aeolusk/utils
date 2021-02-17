package com.celizion.kcg.ems.ftp.config.quartz;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.celizion.kcg.ems.ftp.config.ConfigKeywords;
import com.celizion.kcg.ems.ftp.config.InvalidConfigFormatException;
import com.celizion.kcg.ems.ftp.config.ScheduleContext;
import com.celizion.kcg.ems.ftp.config.file.FileJobFactory;
import com.celizion.kcg.ems.ftp.config.file.LocalFileScanner;
import com.celizion.kcg.ems.ftp.config.file.RemoteFileScanner;
import com.celizion.kcg.ems.ftp.controller.FTPConnectionAlreadyUsedException;
import com.celizion.kcg.ems.ftp.controller.FTPThreadPoolExecutor;
import com.celizion.kcg.ems.ftp.model.FTPJobInfo;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuartzFTPJob implements Job, ConfigKeywords {
	public static final String KEY_FTP_JOB_INFO = "keyFTPJobInfo";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		FTPJobInfo jobInfo = (FTPJobInfo) data.get(KEY_FTP_JOB_INFO);

		if (jobInfo == null) {
			log.warn("Can't find FTP job information.");
			return;
		}

		ScheduleContext scheduleContext = new ScheduleContext();

		try {
			doPre(jobInfo, scheduleContext);
			doFTPJob(jobInfo, scheduleContext);
		} catch (InvalidConfigFormatException e) {
			log.error("{}", e);
		}
	}

	private void doPre(FTPJobInfo jobInfo, ScheduleContext scheduleContext) throws InvalidConfigFormatException {
		String preRules = jobInfo.getRule(KeyPreRule);
		if (preRules == null || "".equals(preRules.trim()))
			return;

		String[] rules = preRules.split(";");
		for (String rule : rules)
			FileJobFactory.createHandler(rule.trim()).accept(jobInfo, scheduleContext);
	}

	private void doPost(FTPJobInfo jobInfo, ScheduleContext scheduleContext) {
		String postRules = jobInfo.getRule(KeyPostRule);
		if (postRules == null || "".equals(postRules.trim()))
			return;
		try {
			String[] rules = postRules.split(";");
			for (String rule : rules)
				FileJobFactory.createHandler(rule.trim()).accept(jobInfo, scheduleContext);
		} catch (InvalidConfigFormatException e) {
			log.warn("invalid post rule(" + postRules + ")");
		}
	}

	private void doFTPJob(FTPJobInfo jobInfo, ScheduleContext scheduleContext) {
		try {
			String inputRule = jobInfo.getRule(KeyToRule);

			switch (jobInfo.type) {
			case UPLOAD:
				doUploadFTPJob(jobInfo, scheduleContext, new LocalFileScanner(jobInfo.getRule(KeyFromRule)), inputRule);
				break;
			case DOWNLOAD:
				doDownloadFTPJob(jobInfo, scheduleContext,
						new RemoteFileScanner(jobInfo.siteInfo, jobInfo.getRule(KeyFromRule)), inputRule);
				break;
			}

		} catch (Exception e) {
			log.error("{}", e);
		}
	}

	private void doUploadFTPJob(FTPJobInfo jobInfo, ScheduleContext scheduleContext, LocalFileScanner scanner,
			String inputRule) throws IOException, InvalidConfigFormatException {
		int index = 0;
		List<File> scanFiles = scanner.scan();
		for (File file : scanFiles) {
			index++;
			scheduleContext.setSourceFilePath(file.getAbsolutePath());
			String remotePath = scheduleContext.translate(inputRule);
			if (index < scanFiles.size())
				FTPThreadPoolExecutor.instance.uploadFile(jobInfo.siteInfo, file, remotePath,
						(isSuccess, args) -> {
							if (isSuccess)
								scheduleContext.addUploadFile(((File) args[1]).getAbsolutePath());
						});
			else
				FTPThreadPoolExecutor.instance.uploadFile(jobInfo.siteInfo, file, remotePath,
						(isSuccess, args) -> {
							if (isSuccess)
								scheduleContext.addUploadFile(((File) args[1]).getAbsolutePath());
							doPost(jobInfo, scheduleContext);
						});
		}
		if (scanFiles.size() == 0)
			log.debug("Can't find any upload file(s). (" + jobInfo.getRule(KeyFromRule) + ")");
	}

	private void doDownloadFTPJob(FTPJobInfo jobInfo, ScheduleContext scheduleContext, RemoteFileScanner scanner,
			String inputRule)
			throws IOException, InvalidConfigFormatException, FTPConnectionAlreadyUsedException, JSchException,
			SftpException {
		int index = 0;
		List<String> scanFiles = scanner.scan();
		for (String remotePath : scanFiles) {
			index++;
			scheduleContext.setSourceFilePath(remotePath);
			String localPath = scheduleContext.translate(inputRule);
			if (index < scanFiles.size())
				FTPThreadPoolExecutor.instance.downloadFile(jobInfo.siteInfo, remotePath, new File(localPath),
						(isSuccess, args) -> {
							if (isSuccess)
								scheduleContext.addDownloadFile(((File) args[2]).getAbsolutePath());
						});
			else
				FTPThreadPoolExecutor.instance.downloadFile(jobInfo.siteInfo, remotePath, new File(localPath),
						(isSuccess, args) -> {
							if (isSuccess)
								scheduleContext.addDownloadFile(((File) args[2]).getAbsolutePath());
							doPost(jobInfo, scheduleContext);
						});
		}
		if (scanFiles.size() == 0)
			log.debug("Can't find any download file(s) or invalid login information. (" + jobInfo.siteInfo.hostname + jobInfo.getRule(KeyFromRule)
					+ ")");
	}
}
