package com.celizion.kcg.ems.ftp.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.celizion.kcg.ems.ftp.model.FTPJobInfo;

public enum FTPJobScheduler {
	instance;

	private Map<String, List<FTPJobInfo>> allFTPJobs = new HashMap<>();

	public void add(String siteName, FTPJobInfo jobInfo) {
		if (jobInfo == null)
			return;
		getSiteJobList(siteName).add(jobInfo);
	}

	private synchronized List<FTPJobInfo> getSiteJobList(String siteName) {
		List<FTPJobInfo> jobs = allFTPJobs.get(siteName);

		if (jobs == null) {
			jobs = new ArrayList<FTPJobInfo>();
			allFTPJobs.put(siteName, jobs);
		}
		return jobs;
	}

	public synchronized void startAllSchedules() {
		for(Entry<String, List<FTPJobInfo>> entry : allFTPJobs.entrySet()) {
			String siteName = entry.getKey();
			for(FTPJobInfo jobInfo : entry.getValue()) {
				jobInfo.getSchedule().start(siteName, jobInfo);
			}
		}
	}
	
	public synchronized void remove(String siteName) {
		allFTPJobs.remove(siteName);
	}

	// for debug.
	public void trace() {
		for (Entry<String, List<FTPJobInfo>> entry : allFTPJobs.entrySet()) {
			System.out.println(">> " + entry.getKey());
			for (FTPJobInfo jobInfo : entry.getValue()) {
				System.out.println("   - " + jobInfo);
			}
			System.out.println();
		}
	}
}
