package com.celizion.kcg.ems.ftp.config.file;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

import com.celizion.kcg.ems.ftp.config.ConfigKeywords;
import com.celizion.kcg.ems.ftp.config.InvalidConfigFormatException;
import com.celizion.kcg.ems.ftp.config.ScheduleContext;
import com.celizion.kcg.ems.ftp.model.FTPJobInfo;
import com.celizion.kcg.ems.ftp.util.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileJobFactory implements ConfigKeywords {
	public static BiConsumer<FTPJobInfo, ScheduleContext> createHandler(final String rule)
			throws InvalidConfigFormatException {
		// Move Function
		if (rule.startsWith(keywordMove))
			return createMoveFunction(rule.substring(keywordMove.length(), rule.length()));

		// Delete Function
		if (rule.startsWith(keywordDelete))
			return createDeleteFunction(rule.substring(keywordDelete.length(), rule.length()));

		// Log Function
		if (rule.startsWith(keywordLog))
			return createLogFunction(rule.substring(keywordLog.length(), rule.length()));

		throw new InvalidConfigFormatException("unknown rule(" + rule + ")");
	}

	private static BiConsumer<FTPJobInfo, ScheduleContext> createMoveFunction(final String rule) {
		return (ftpJobInfo, scheduleContext) -> {
			// ex) {DOWNLOADFILES},/temp/bono/backup/{SOURCEFILE}.move
			String[] args = rule.split(",");

			if (args.length < 2)
				return;

			switch (args[0].trim()) {
			case KeywordDownloadFiles:
				for (String path : scheduleContext.getDownloadFiles())
					move(path, args[1], scheduleContext);
				break;
			case KeywordUploadFiles:
				for (String path : scheduleContext.getUploadFiles())
					move(path, args[1], scheduleContext);
				break;
			default:
				// specified path.
				move(args[0], args[1], scheduleContext);
				break;
			}
		};
	}

	private static void move(String from, String to, ScheduleContext scheduleContext) {
		try {
			scheduleContext.setFilePath(from);
			String movePath = scheduleContext.translate(to);
			log.info("move from " + from + " to " + movePath);
			FileUtils.makeDirectory(FileUtils.getDirectory(movePath));
			(new File(from)).renameTo(new File(movePath));
		} catch (InvalidConfigFormatException e) {
			log.warn("Can't move file. invalid rule(" + to + ")");
		}
	}

	private static BiConsumer<FTPJobInfo, ScheduleContext> createLogFunction(final String rule) {
		return (ftpJobInfo, scheduleContext) -> {
			try {
				log.info(scheduleContext.translate(rule));
			} catch (InvalidConfigFormatException e) {
				log.warn("invalid log format (" + rule + ")");
			}
		};
	}
	
	private static BiConsumer<FTPJobInfo, ScheduleContext> createDeleteFunction(final String rule) {
		return (ftpJobInfo, scheduleContext) -> {
			// ex) {FILES:directory,pattern}
			if(!rule.startsWith(KeywordFilesPrefix) || !rule.endsWith("}")) 
				return;

			String findFileRule = rule.substring(KeywordFilesPrefix.length(), rule.length() - 1);
			String[] args = findFileRule.split(",");

			if (args.length < 2)
				return;

			try {
				for(File file: FileUtils.listFiles(new File(args[0]), args[1], false)) {
					file.delete();
					log.info("delete file " + file.getAbsolutePath());
				}
			} catch (IOException e) {
				log.warn("Can't get filelist in " + args[0]);
			}
		};
	}
}
