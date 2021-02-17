package com.celizion.kcg.ems.ftp.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.celizion.kcg.ems.ftp.controller.LoginInfoManager;
import com.celizion.kcg.ems.ftp.model.FTPJobInfo;
import com.celizion.kcg.ems.ftp.model.FTPJobType;
import com.celizion.kcg.ems.ftp.model.FTPSiteInfo;
import com.celizion.kcg.ems.ftp.util.PasswordEncoder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FTPJobLoader implements ConfigKeywords {
	instance;

	private static AtomicInteger jobSequence = new AtomicInteger(1);
	public static final int MAX_CONFIG_SIZE = 10240000;
	public static final int DEFAULT_FTP_PORT = 21;
	public static final int DEFAULT_SFTP_PORT = 21;

	public void load(File jsonFile) throws IOException {
		String jsonConfig = getConfigData(jsonFile);

		JsonElement top = JsonParser.parseString(jsonConfig);
		JsonElement sites = top.getAsJsonObject().get("sites");

		sites.getAsJsonArray().forEach(site -> {
			String siteName = site.getAsString();

			JsonElement siteElement = top.getAsJsonObject().get(siteName);
			if (siteElement == null) {
				log.warn("Can't find site [" + siteName + "] information.");
				return;
			}

			try {
				String id = JsonUtils.findAnd2String(siteElement, "id");
				String password = getPassword(JsonUtils.findAnd2String(siteElement, "password"));
				boolean enableSFTP = JsonUtils.isTrue(siteElement, "sftp");
				int port = JsonUtils.findAnd2Int(siteElement, "port",
						(enableSFTP ? DEFAULT_SFTP_PORT : DEFAULT_FTP_PORT));
				FTPSiteInfo siteInfo = new FTPSiteInfo(siteName, JsonUtils.findAnd2String(siteElement, "hostname"),
						port, enableSFTP);

				JsonElement worksElement = siteElement.getAsJsonObject().get("works");

				if (id == null || password == null || "".equals(id) || "".equals(password)) {
					throw new InvalidConfigFormatException(
							"Invalid id or password. (id=" + id + ", password=" + password + ")");
				}

				LoginInfoManager.instance.add(siteInfo.hostname, id, password);

				if (worksElement.isJsonArray()) {
					worksElement.getAsJsonArray().forEach(element -> {
						FTPJobScheduler.instance.add(siteName, loadFtpWorkInformation(siteInfo, element));
					});
				}
			} catch (Exception e) {
				log.error("{}", e);
			}
		});

		// debug
		FTPJobScheduler.instance.trace();
	}

	private String getPassword(String password) throws Exception {
		if (password.startsWith(keywordEncrypt)) {
			return PasswordEncoder.decrypt(password.substring(keywordEncrypt.length(), password.length()));
		}
		return password;
	}

	private FTPJobInfo loadFtpWorkInformation(FTPSiteInfo siteInfo, JsonElement element) {
		String jobName = JsonUtils.findAnd2String(element, "name");
		FTPJobType jobType = FTPJobType.string2Type(JsonUtils.findAnd2String(element, "jobType"));
		Schedule schedule = ScheduleFactory.build(createScheduleId(jobName),
				JsonUtils.findAnd2String(element, "schedule"));

		if (jobType == null || schedule == null) {
			log.warn("Can't find(or invalid) jobType or schedule property in site[" + siteInfo.aliasName
					+ "] configuration.");
			return null;
		}

		return FTPJobInfo.create(siteInfo, jobType).schedule(schedule)
				.addRule(KeyFromRule, JsonUtils.findAnd2String(element, KeyFromRule))
				.addRule(KeyToRule, JsonUtils.findAnd2String(element, KeyToRule))
				.addRule(KeyPreRule, JsonUtils.findAnd2String(element, KeyPreRule))
				.addRule(KeyPostRule, JsonUtils.findAnd2String(element, KeyPostRule));
	}

	private synchronized String createScheduleId(String jobName) {
		int sequence = jobSequence.getAndAdd(1);
		if (jobName == null || jobName.equals(""))
			return sequence + "[UNDEFINED]";
		return sequence + "[" + jobName + "]";
	}

	private String getConfigData(File jsonFile) throws IOException {
		try (FileInputStream inStream = new FileInputStream(jsonFile)) {
			long fileSize = jsonFile.length();

			if (fileSize > MAX_CONFIG_SIZE) {
				throw new IOException("Config file size too big.(limit = " + MAX_CONFIG_SIZE + " bytes)");
			}

			byte[] buf = new byte[(int) fileSize];

			inStream.read(buf);
			return new String(buf);
		}
	}
}
