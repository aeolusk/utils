package com.celizion.kcg.ems.ftp.config;

import com.celizion.kcg.ems.ftp.model.FTPJobInfo;

public interface Schedule {
	public boolean initialize(String scheduleId, String scheduleDescription);
	
	public String getScheduleId();

	public void start(String siteName, FTPJobInfo jobInfo);

	public void stop(String siteName, FTPJobInfo jobInfo);
}
