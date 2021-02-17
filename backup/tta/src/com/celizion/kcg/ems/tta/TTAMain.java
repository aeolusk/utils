package com.celizion.kcg.ems.tta;

import com.celizion.kcg.ems.tta.controller.NEHandler;
import com.celizion.kcg.ems.tta.controller.handler.DefaultUserHandler;
import com.celizion.kcg.ems.tta.define.TTAPort;
import com.celizion.kcg.ems.tta.simulator.FMSimulator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TTAMain {
	public static void main(String[] args) {
		org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TTAMain.class);
		final NEHandler neHandler = new NEHandler();

		logger.debug(">>> TTAMain <<<");
		try {
			neHandler.start(TTAPort.PORT_ALARM, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_COMMAND, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_HISTORY_PERF, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_REALTIME_PERF, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_CONFIG, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_USERINFO_OUTPUT, DefaultUserHandler.createDummyUserHandler());
			neHandler.start(TTAPort.PORT_USERINFO_REQUEST, DefaultUserHandler.createDummyUserHandler());

			// for testing.
			FMSimulator.makeTestAlarm();
		} catch (InterruptedException e) {
			log.error("{}", e);
		}
	}

}
