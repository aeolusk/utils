package com.celizion.kcg.ems.tta.define;

public interface TTAPort {
	public final int PORT_ALARM = 8282;
	public final int PORT_REALTIME_PERF = 8283;
	public final int PORT_HISTORY_PERF = 8284;
	public final int PORT_CONFIG = 8285;
	public final int PORT_COMMAND = 8286;
	public final int PORT_USERINFO_OUTPUT = 8288;
	public final int PORT_USERINFO_REQUEST = 8289;
	
	public static byte getPORTType(int port) {
		switch(port) {
		case PORT_ALARM: return 0x01;
		case PORT_REALTIME_PERF: return 0x02;
		case PORT_HISTORY_PERF: return 0x03;
		case PORT_CONFIG: return 0x04;
		case PORT_COMMAND: return 0x05;
		// backup data port dosen't use anymore.
		case PORT_USERINFO_OUTPUT: return 0x07;
		case PORT_USERINFO_REQUEST: return 0x08;
		}
		return 0x00;
	}
	
	public static byte getMSGType(int port) {
		switch(port) {
		case PORT_ALARM: return 0x01;
		case PORT_REALTIME_PERF: return 0x02;
		case PORT_HISTORY_PERF: return 0x03;
		case PORT_CONFIG: return 0x04;
		// backup data port dosen't use anymore.
		case PORT_USERINFO_OUTPUT: return 0x06;
		}
		return 0x00;
	}
}
