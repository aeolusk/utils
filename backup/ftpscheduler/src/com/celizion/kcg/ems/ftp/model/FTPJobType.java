package com.celizion.kcg.ems.ftp.model;

public enum FTPJobType {
	DOWNLOAD("download"), UPLOAD("upload");
	
	private final String keyword;
	
	private FTPJobType(String keyword) {
		this.keyword = keyword;
	}
	
	public static FTPJobType string2Type(String input) {
		for(FTPJobType jobType : FTPJobType.values()) {
			if(jobType.keyword.equals(input))
				return jobType;
		}
		return null;
	}
}
