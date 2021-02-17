package com.celizion.kcg.ems.tta.simulator;

import java.util.Timer;
import java.util.TimerTask;

import com.celizion.kcg.ems.tta.controller.ConnectionContext;
import com.celizion.kcg.ems.tta.controller.ConnectionManager;
import com.celizion.kcg.ems.tta.controller.handler.TTAProtocolHandler;
import com.celizion.kcg.ems.tta.define.TTAPort;

public class FMSimulator {
	public static void makeTestAlarm() {
		(new Timer()).schedule(new TimerTask() {

			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					for (ConnectionContext context : ConnectionManager.instance
							.findConnectedClient(TTAPort.PORT_ALARM)) {
						System.out.println(">>>>>>>>>>>>>>> SEND SAMPLE ALARM <<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
						TTAProtocolHandler.sendOutputMsg(context, FMSimulator.getCybertelAlarmPOD());
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}, 10000);
	}

	public static String getCybertelAlarmPOD() {
		StringBuilder sb = new StringBuilder();

		sb.append("    PS1_LSM01A 2019-02-19 16:54:29.091\n");
		sb.append("*** A1002 CPU OVERLOAD ALARM OCCURRED\n");
		sb.append("      NETWORKELEMENT    	= PS1_LSM01A\n");
		sb.append("      LOCATION          	= /A-SIDE/CPU#3\n");
		sb.append("      EVENTTYPE         	= QUALITY OF SERVICE ALARM\n");
		sb.append("      PROBABLECAUSE     	= SYSTEM RESOURCE OVERLOAD\n");
		sb.append("      SPECIFICPROBLEM   	= CPU OVERLOAD\n");
		sb.append("      PERCEIVEDSEVERITY 	= CRITICAL\n");
		sb.append("      ADDITIONALTEXT    	= CURRENT CPU USAGE IS 99%\n");
		sb.append("    COMPLETED\n");
		return sb.toString();

	}
	
	public static String getContelaAlarmPOD() {
		StringBuilder sb = new StringBuilder();

		sb.append("***:CRI A11002 EMS_SPGW_INACCESS\n");
		sb.append("      LOCATION ONEBOXEPC00/SPGW00\n");
		sb.append("      GRADE CRITICAL\n");
		sb.append("      EVENTTYPE Communications\n");
		sb.append("      PROBABLECAUSE Connection Establishment Error\n");
		sb.append("      ADDITIONAL INFO TCP Connection Fail\n");
		sb.append("      OCCURRED TIME 2020/12/23 05:45:35\n");
		sb.append("    COMPLETED\n");
		return sb.toString();
	}
	

	// 1 2
	// 123456789012345678901234567890
	// KUROEMS 2001-02-03 13:34:56
	// *** A3456 CPU ALARAM
	// LOC = A_UNIT/MPU/CPU1
	// SERVERITY = CRITICAL
	// COMPLETED
	//
	// ***:CRI A11002 EMS_SPGW_INACCESS
	// LOCATION ONEBOXEPC00/SPGW00
	// GRADE CRITICAL
	// EVENTTYPE Communications
	// PROBABLECAUSE Connection Establishment Error
	// ADDITIONAL INFO TCP Connection Fail
	// OCCURRED TIME 2020/12/23 05:45:35
	// COMPLETED
	//
	// <알람 해제 시>
	// ###:CRI A11002 EMS_SPGW_INACCESS
	// LOCATION ONEBOXEPC00/SPGW00
	// GRADE CRITICAL
	// EVENTTYPE Communications
	// PROBABLECAUSE Connection Establishment Error
	// ADDITIONAL INFO TCP Connection Fail
	// OCCURRED TIME 2020/12/23 05:45:37
	// COMPLETED
	//
	// PS1_LSM01A 2019-02-19 16:54:29.091
	// *** A1002 CPU OVERLOAD ALARM OCCURRED
	// NETWORKELEMENT = PS1_LSM01A
	// LOCATION = /A-SIDE/CPU#3
	// EVENTTYPE = QUALITY OF SERVICE ALARM
	// PROBABLECAUSE = SYSTEM RESOURCE OVERLOAD
	// SPECIFICPROBLEM = CPU OVERLOAD
	// PERCEIVEDSEVERITY = CRITICAL
	// ADDITIONALTEXT = CURRENT CPU USAGE IS 99%
	// COMPLETED
	//
	// PS1_LSM01A 2019-02-19 16:58:18.024
	// ### A1002 CPU OVERLOAD ALARM CLEARED
	// NETWORKELEMENT = PS1_LSM01A
	// LOCATION = /A-SIDE/CPU#3
	// EVENTTYPE = QUALITY OF SERVICE ALARM
	// PROBABLECAUSE = SYSTEM RESOURCE OVERLOAD
	// SPECIFICPROBLEM = CPU OVERLOAD
	// PERCEIVEDSEVERITY = CRITICAL
	// ADDITIONALTEXT = CURRENT CPU USAGE IS 50%
	// COMPLETED

}
