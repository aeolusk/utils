package com.celizion.kcg.ems.ftp;

import java.io.File;
import java.io.IOException;

import com.celizion.kcg.ems.ftp.config.FTPJobLoader;
import com.celizion.kcg.ems.ftp.config.FTPJobScheduler;
import com.celizion.kcg.ems.ftp.controller.mgmt.FTPConnectionPool;

public class FtpSchedulerMain {

	public static void pringUsageAndExit() {
		System.out.println("Usage: java -jar [executable jar path] [config path]");
		System.exit(0);
	}

	public static void main(String[] args) throws IOException {
		if (args == null || args.length == 0)
			pringUsageAndExit();

		File configFile = new File(args[0]);
		if (!configFile.exists() || !configFile.isFile())
			pringUsageAndExit();

		FTPJobLoader.instance.load(configFile);

		FTPConnectionPool.instance.startUnusedFTPClientRemover();
		FTPJobScheduler.instance.startAllSchedules();
	}

}
