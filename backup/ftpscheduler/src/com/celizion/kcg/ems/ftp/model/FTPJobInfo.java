package com.celizion.kcg.ems.ftp.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.celizion.kcg.ems.ftp.config.Schedule;

public class FTPJobInfo {
	public final FTPSiteInfo siteInfo;
	public final FTPJobType type;
	private Schedule schedule;
	private Map<String, String> rules = new ConcurrentHashMap<>();

	
	public FTPJobInfo(FTPSiteInfo siteInfo, FTPJobType type) {
		this.siteInfo = siteInfo;
		this.type = type;
	}

	public static FTPJobInfo create(FTPSiteInfo siteInfo, FTPJobType type) {
		return new FTPJobInfo(siteInfo, type);
	}

	public FTPJobInfo schedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public FTPJobInfo addRule(String ruleName, String ruleDescription) {
		rules.put(ruleName, ruleDescription);
		return this;
	}

	public FTPJobType getType() {
		return type;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getRule(String key) {
		return rules.get(key);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("FTPJobInfo [type=" + type);
		sb.append(", site=" + siteInfo);
		sb.append(", schedule=" + schedule);
		for (Entry<String, String> entry : rules.entrySet()) {
			sb.append(", " + entry.getKey() + " = " + entry.getValue());
		}
		sb.append("]");
		return sb.toString();
	}
}
