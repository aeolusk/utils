package com.celizion.kcg.ems.ftp.model;

public class FTPSiteInfo {
	public final String aliasName;
	public final String hostname;
	public final int port;
	public final boolean enableSFTP;

	public FTPSiteInfo(String aliasName, String hostname, int port, boolean enableSFTP) {
		this.aliasName = aliasName;
		this.hostname = hostname;
		this.port = port;
		this.enableSFTP = enableSFTP;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aliasName == null) ? 0 : aliasName.hashCode());
		result = prime * result + (enableSFTP ? 1231 : 1237);
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FTPSiteInfo other = (FTPSiteInfo) obj;
		if (aliasName == null) {
			if (other.aliasName != null)
				return false;
		} else if (!aliasName.equals(other.aliasName))
			return false;
		if (enableSFTP != other.enableSFTP)
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (port != other.port)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FTPSiteInfo [aliasName=" + aliasName + ", hostname=" + hostname + ", port=" + port + ", enableSFTP="
				+ enableSFTP + "]";
	}
}
