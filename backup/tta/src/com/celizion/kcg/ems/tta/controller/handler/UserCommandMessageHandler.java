package com.celizion.kcg.ems.tta.controller.handler;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;
import com.celizion.kcg.ems.tta.define.TTAPort;

public class UserCommandMessageHandler extends TTAProtocolHandler {

	@Override
	protected void handleMessage(ConnectionContext context, Message message) {
		switch (message.ptype) {
		case InputCommand:
			context.writeAndFlush(MessageBuilder.portTypeConfirm(TTAPort.getPORTType(context.port), context.NEIndex));
			break;
		case PortTypeConfirm:
			context.writeAndFlush(MessageBuilder.portTypeConfirmAck());
			break;
		case PortTypeConfirmAck:
			byte msgType = TTAPort.getMSGType(context.port);
			if (msgType != 0x00)
				context.writeAndFlush(MessageBuilder.startMessageTransmission(msgType));
			break;
		default:
			// do nothing.
		}

	}

}
