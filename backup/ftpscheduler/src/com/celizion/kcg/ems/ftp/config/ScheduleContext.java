package com.celizion.kcg.ems.ftp.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.celizion.kcg.ems.ftp.util.FileNameInfo;
import com.celizion.kcg.ems.ftp.util.FileUtils;

public class ScheduleContext implements ConfigKeywords {
	private Map<String, Object> variables = new HashMap<>();
	private Map<String, Object> unmodifiableVariables = Collections.unmodifiableMap(variables);

	private synchronized void addVariable(String key, Object value) {
		variables.put(key, value);
	}

	public void setSourceFilePath(String filepath) {
		addVariable(KeywordSourceFilePath, filepath);
		addVariable(KeywordSourceFileName, FileUtils.getFilename(filepath));
	}
	
	public void setFilePath(String filepath) {
		addVariable(KeywordFilePath, filepath);
		addVariable(KeywordFileName, FileUtils.getFilename(filepath));
	}

	public synchronized void addUploadFile(String filepath) {
		@SuppressWarnings("unchecked")
		List<String> uploadFiles = (List<String>) variables.get(KeywordUploadFiles);

		if (uploadFiles == null) {
			uploadFiles = new ArrayList<String>();
			variables.put(KeywordUploadFiles, uploadFiles);
		}
		uploadFiles.add(filepath);
	}

	public synchronized void addDownloadFile(String filepath) {
		@SuppressWarnings("unchecked")
		List<String> downloadFiles = (List<String>) variables.get(KeywordDownloadFiles);

		if (downloadFiles == null) {
			downloadFiles = new ArrayList<String>();
			variables.put(KeywordDownloadFiles, downloadFiles);
		}
		downloadFiles.add(filepath);
	}

	public List<String> getUploadFiles() {
		@SuppressWarnings("unchecked")
		List<String> uploadFiles = (List<String>) variables.get(KeywordUploadFiles);

		if (uploadFiles == null)
			return Collections.emptyList();
		return uploadFiles;

	}

	public List<String> getDownloadFiles() {
		@SuppressWarnings("unchecked")
		List<String> downloadFiles = (List<String>) variables.get(KeywordDownloadFiles);

		if (downloadFiles == null)
			return Collections.emptyList();
		return downloadFiles;
	}

	public synchronized Object getVariable(String key) {
		return variables.get(key);
	}

	public synchronized Map<String, Object> getVariables() {
		return unmodifiableVariables;
	}

	public String translate(String input) throws InvalidConfigFormatException {
		return translateReserved(input, getVariables());
	}

	private String translateReserved(String input, Map<String, Object> variableMap)
			throws InvalidConfigFormatException {
		String result = input;
		result = translateSourceFile(result, variableMap);
		result = translateFile(result, variableMap);
		result = translateSimpleDateFormat(result);
		result = translateUploadFiles(result);
		result = translateDownloadFiles(result);
		return result;
	}

	private String fileList2String(List<String> filepaths) {
		Map<String, List<String>> path2files = new HashMap<>();

		for (String filepath : filepaths) {
			FileNameInfo nameInfo = FileNameInfo.create(filepath);
			List<String> filenames = path2files.get(nameInfo.path);
			if (filenames == null) {
				filenames = new ArrayList<String>();
				path2files.put(nameInfo.path, filenames);
			}
			filenames.add(nameInfo.name);
		}

		StringBuilder sb = new StringBuilder();
		for (Entry<String, List<String>> entry : path2files.entrySet()) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(entry.getKey() + "[");
			int length = sb.length();
			for (String name : entry.getValue()) {
				if (sb.length() > length)
					sb.append(",");
				sb.append(name);
			}
			sb.append("]");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private String translateUploadFiles(String input) {
		// {UPLOADFILES}
		int idx = input.indexOf(KeywordUploadFiles);
		if (idx < 0)
			return input;

		Object value = getVariable(KeywordUploadFiles);
		if (value != null && value instanceof List) {
			return input.replace(KeywordUploadFiles, fileList2String((List<String>) value));
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	private String translateDownloadFiles(String input) {
		// {DOWNLOADFILES}
		int idx = input.indexOf(KeywordDownloadFiles);
		if (idx < 0)
			return input;

		Object value = getVariable(KeywordDownloadFiles);
		if (value != null && value instanceof List) {
			return input.replace(KeywordDownloadFiles, fileList2String((List<String>) value));
		}
		return input;
	}

	private String translateSimpleDateFormat(String input) throws InvalidConfigFormatException {
		// ex) {SIMPLEDATEFORMAT:yyyyMMddYYHHMM}
		int idx = input.indexOf(KeywordSimpleDateFormatPrefix);
		int endIdx = 0;

		if (idx < 0)
			return input;

		endIdx = input.indexOf("}", idx);

		if (endIdx < 0)
			throw new InvalidConfigFormatException("Missing '}' in " + input);

		SimpleDateFormat format = new SimpleDateFormat(
				input.substring(idx + KeywordSimpleDateFormatPrefix.length(), endIdx));
		String dateString = format.format(new Date(System.currentTimeMillis()));

		return input.replace(input.substring(idx, endIdx + 1), dateString);
	}

	private String translateSourceFile(String input, Map<String, Object> variableMap) {
		return _translateFile(input, variableMap, KeywordSourceFilePath, KeywordSourceFileName);
	}

	private String translateFile(String input, Map<String, Object> variableMap) {
		return _translateFile(input, variableMap, KeywordFilePath, KeywordFileName);
	}
	
	private String _translateFile(String input, Map<String, Object> variableMap, String keywordFilePath, String keywordFileName) {
		// {SOURCEFILE}
		String result = input;
		int idx = input.indexOf(keywordFilePath);

		if (idx > 0) {
			if (variableMap.containsKey(keywordFilePath))
				result = result.replace(keywordFilePath, variableMap.get(keywordFilePath).toString());
		}

		idx = input.indexOf(keywordFileName);
		if (idx > 0) {
			if (variableMap.containsKey(keywordFileName))
				result = result.replace(keywordFileName, variableMap.get(keywordFileName).toString());
		}

		return result;
	}
}
