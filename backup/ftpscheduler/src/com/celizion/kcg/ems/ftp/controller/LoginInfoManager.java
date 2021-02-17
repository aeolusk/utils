package com.celizion.kcg.ems.ftp.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.celizion.kcg.ems.ftp.model.LoginInfo;

public enum LoginInfoManager {
	instance;

	private Map<String, LoginInfo> loginInfos = new ConcurrentHashMap<>();

	public LoginInfo add(String host, String user, String password) {
		LoginInfo info = loginInfos.get(host);
		if (info != null)
			return info;

		info = new LoginInfo(user, password);
		loginInfos.put(host, info);
		return info;
	}

	public LoginInfo get(String host) {
		return loginInfos.get(host);
	}
}
