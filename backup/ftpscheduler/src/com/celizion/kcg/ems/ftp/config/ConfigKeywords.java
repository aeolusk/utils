package com.celizion.kcg.ems.ftp.config;

public interface ConfigKeywords {
	// Used for site definition.
	public static final String KeyFromRule = "from";
	public static final String KeyToRule = "to";
	public static final String KeyPreRule = "preRule";
	public static final String KeyPostRule = "postRule";
	
	// Used for variable part definition.
	public final String KeywordSimpleDateFormatPrefix = "{SIMPLEDATEFORMAT:";
	public final String KeywordSourceFilePath = "{SOURCEFILEPATH}"; // ex) /directory/file.ext
	public final String KeywordSourceFileName = "{SOURCEFILENAME}"; // ex) file.ext
	public final String KeywordFilePath = "{FILEPATH}"; // ex) /directory/file.ext
	public final String KeywordFileName = "{FILENAME}"; // ex) file.ext
	public final String KeywordUploadFiles = "{UPLOADFILES}";
	public final String KeywordDownloadFiles = "{DOWNLOADFILES}";
	public final String KeywordFilesPrefix = "{FILES:"; // usage) {FILES:directory,file_pattern}
	
	// Used for local(or remote) source files definition.
	public final String KeywordFilePattern = "filePattern:";
	public final String KeywordFile = "file:";
	public final String keywordDelete = "delete:";
	public final String keywordMove = "move:";
	public final String keywordLog = "log:";
	
	public final String keywordEncrypt = "encrypt:";
}
