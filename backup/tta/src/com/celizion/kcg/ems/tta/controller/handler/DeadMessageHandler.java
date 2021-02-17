package com.celizion.kcg.ems.tta.controller.handler;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadMessageHandler extends TTAProtocolHandler {

	@Override
	protected void handleMessage(ConnectionContext context, Message message) {
		log.debug("Can't handled message (" + message + ")");
	}

}
