package com.celizion.kcg.ems.tta.controller.handler;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;
import com.celizion.kcg.ems.tta.define.ActionParameter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUserHandler {
	public static BiConsumer<ConnectionContext, Message> createDummyUserHandler() {
		return createCommandHandler((command) -> {
			return "input command is " + command; // echo
		}, (context, output) -> {
			log.debug(output); // log output.
		});
	}

	public static BiConsumer<ConnectionContext, Message> createCommandHandler(Function<String, String> commandHandler,
			BiConsumer<ConnectionContext, String> outputHandler) {
		return (context, message) -> {
			switch (message.ptype) {
			case InputCommand:
				if (commandHandler != null) {
					String reply = null;
					try {
						reply = commandHandler.apply(message.getParameter(ActionParameter.InOutString).toString());
					} catch (Exception e) {
						log.warn("Can't handle command (" + message.getParameter(ActionParameter.InOutString) + ")");
					}
					if (reply != null) {
						TTAProtocolHandler.sendInputCommandAck(context, message.messageId, true,
								message.getParameter(ActionParameter.InOutString).toString());
						TTAProtocolHandler.sendCommandOutputMsg(context, message.messageId, reply);
					} else {
						TTAProtocolHandler.sendInputCommandAck(context, message.messageId, false,
								message.getParameter(ActionParameter.InOutString).toString());
					}
				}
				break;
			case OutputMsg:
				if (outputHandler != null) {
					outputHandler.accept(context, message.getParameter(ActionParameter.InOutString).toString());
				}
				break;
			default:
				// do nothing.
			}
		};
	}
}
