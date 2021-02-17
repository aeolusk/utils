package com.celizion.kcg.ems.ftp.config.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.celizion.kcg.ems.ftp.config.Schedule;
import com.celizion.kcg.ems.ftp.model.FTPJobInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuartzSchedule implements Schedule {
	private String cronSchedule;
	private String scheduleId;

	@Override
	public boolean initialize(String scheduleId, String cronSchedule) {
		this.scheduleId = scheduleId;
		this.cronSchedule = cronSchedule;
		return true;
	}

	@Override
	public String getScheduleId() {
		return scheduleId;
	}

	@Override
	public void start(String siteName, FTPJobInfo jobInfo) {
		log.info("start ftp scheduler. site=" + siteName + ", job=" + jobInfo);

		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();

			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("TRIGGER" + scheduleId)
					.withSchedule(cronSchedule(cronSchedule)).build();
			
			JobDetail job = JobBuilder.newJob(QuartzFTPJob.class)
				    .withIdentity(scheduleId)
				    .build();
			job.getJobDataMap().put(QuartzFTPJob.KEY_FTP_JOB_INFO, jobInfo);
			
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("{}", e);
		}
	}

	@Override
	public void stop(String siteName, FTPJobInfo jobInfo) {
		// TODO Auto-generated method stub

	}

	private static CronScheduleBuilder cronSchedule(String cronExpression) {
		return CronScheduleBuilder.cronSchedule(cronExpression);
	}

	@Override
	public String toString() {
		return "QuartzSchedule [scheduleDesc=" + cronSchedule + "]";
	}
}
