package com.celizion.kcg.ems.ftp.config;

import com.celizion.kcg.ems.ftp.config.quartz.QuartzSchedule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScheduleFactory {
	public static Schedule build(String scheduleId, String scheduleDesc) {
		String prefix = ConfigUtils.prefix(scheduleDesc, ':');

		switch (prefix) {
		case "quartz":
			QuartzSchedule schedule = new QuartzSchedule();

			if (schedule.initialize(scheduleId, scheduleDesc.substring(7, scheduleDesc.length())));
				return schedule;
		default:
			log.error("Can't understand schedule description.(" + scheduleDesc + ")");
		}
		return null;
	}

}
