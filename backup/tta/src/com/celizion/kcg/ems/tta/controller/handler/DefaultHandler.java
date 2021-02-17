package com.celizion.kcg.ems.tta.controller.handler;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;
import com.celizion.kcg.ems.tta.controller.ConnectionManager;
import com.celizion.kcg.ems.tta.define.TTAPort;

public class DefaultHandler extends TTAProtocolHandler {

	@Override
	protected void handleMessage(ConnectionContext context, Message message) {
		switch (message.ptype) {
		case Control_ACCEPT:
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
		case AppStatusCheck:
			context.writeAndFlush(MessageBuilder.appStatusCheckAck());
			break;
		case ClosePortConnection:
			context.writeAndFlush(MessageBuilder.closePortConnectionAck());
			break;
		case ClosePortConnectionAck:
			ConnectionManager.instance.handleClosePortConnectionAck(context);
			break;
		default:
			// do nothing.
		}

	}

}
