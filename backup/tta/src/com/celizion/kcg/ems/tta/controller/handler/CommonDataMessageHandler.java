package com.celizion.kcg.ems.tta.controller.handler;

import java.util.function.BiConsumer;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonDataMessageHandler extends TTAProtocolHandler {
	private BiConsumer<ConnectionContext, Message> dummyHandler = DefaultUserHandler.createDummyUserHandler();

	@Override
	protected void handleMessage(ConnectionContext context, Message message) {
		switch (message.ptype) {
		case StartMsgTransmission:
			context.writeAndFlush(MessageBuilder.startMsgTransmissionAck());
			context.enableTransmission();
			break;
		case StartMsgTransmissionAck:
			context.startClientStatusCheck();
			break;
		case StopMsgTransmission:
			context.stopMessageTransmission();
			break;
		case InputCommand:
		case OutputMsg:
			try {
				if (!context.handleMessage(message)) {
					dummyHandler.accept(context, message);
				}
			} catch (Exception e) {
				log.warn("Can't handle message because (" + e.getClass().getName() + ":" + e.getMessage() + ")");
			}
			break;
		default:
			// do nothing.
		}

	}

}