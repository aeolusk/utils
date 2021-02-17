package com.celizion.kcg.ems.tta.controller.handler;

import java.util.HashMap;
import java.util.Map;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.ConnectionContext;
import com.celizion.kcg.ems.tta.controller.ConnectionManager;
import com.celizion.kcg.ems.tta.define.PType;
import com.celizion.kcg.ems.tta.define.TTAPort;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TTAProtocolHandler {
	private static Map<Integer, TTAProtocolHandler> handlerMap = new HashMap<>();
	private static TTAProtocolHandler defHandler;
	private static TTAProtocolHandler deadMsgHandler = new DeadMessageHandler();

	protected abstract void handleMessage(ConnectionContext context, Message message);

	private static synchronized void createHandlers() {
		if (defHandler != null)
			return;

		defHandler = new DefaultHandler();

		handlerMap.put(TTAPort.PORT_ALARM, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_REALTIME_PERF, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_HISTORY_PERF, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_CONFIG, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_COMMAND, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_USERINFO_OUTPUT, new CommonDataMessageHandler());
		handlerMap.put(TTAPort.PORT_USERINFO_REQUEST, new DeadMessageHandler());
	}

	public static void handleMessage(Channel ch, Message message) {
		createHandlers();

		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null) {
			TTAProtocolHandler handler = handlerMap.get(context.port);

			if (handler != null)
				handler.handleMessage(context, message);

			defHandler.handleMessage(context, message);
			
			context.doPostReply(message);
		} else {
			deadMsgHandler.handleMessage(context, message);
		}
	}

	public static void sendStartMsgTransmission(Channel ch) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendStartMsgTransmission(context);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendStartMsgTransmission(ConnectionContext context) {
		if (context != null) {
			byte msgType = TTAPort.getMSGType(context.port);
			if (msgType != 0x00) {
				context.writeAndFlush(MessageBuilder.startMessageTransmission(msgType));
			}
		}
	}
	
	public static void sendStopMsgTransmission(Channel ch) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendStopMsgTransmission(context);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendStopMsgTransmission(ConnectionContext context) {
		if (context != null) {
			byte msgType = TTAPort.getMSGType(context.port);
			if (msgType != 0x00) {
				context.writeAndFlush(MessageBuilder.stopMessageTransmission(msgType));
			}
		}
	}

	public static void sendInputCommand(Channel ch, String command) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendInputCommand(ConnectionManager.instance.find(ch), command);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendInputCommand(ConnectionContext context, String command) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.inputCommand(command));
		}
	}

	public static void sendInputCommandAck(Channel ch, int reqMsgId, boolean isValid, String command) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendInputCommandAck(ConnectionManager.instance.find(ch), reqMsgId, isValid, command);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendInputCommandAck(ConnectionContext context, int reqMsgId, boolean isValid, String command) {
		if (context != null) {
			context.enableTransmission(); 
			context.writeAndFlush(MessageBuilder.inputCommandAck(reqMsgId, isValid, command));
		}
	}

	public static void sendOutputMsg(Channel ch, String output) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendOutputMsg(context, output);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendCommandOutputMsg(ConnectionContext context, int reqMsgId, String output) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.outputMsg(reqMsgId, output));
		}
	}
	
	public static void sendOutputMsg(ConnectionContext context, String output) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.outputMsg(output));
		}
	}

	public static void sendAppStatusCheck(Channel ch) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendAppStatusCheck(context);
		else
			log.warn("Can't find context for " + ch);
	}

	public static void sendAppStatusCheck(ConnectionContext context) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.appStatusCheck());
		}
	}

	public static void sendAppStatusCheckAck(Channel ch) {
		ConnectionContext context = ConnectionManager.instance.find(ch);
		if (context != null)
			sendAppStatusCheckAck(context);
		else
			log.warn("Can't find context for " + ch);

	}

	public static void sendAppStatusCheckAck(ConnectionContext context) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.appStatusCheckAck());
		}
	}

	public static void sendClosePortConnection(ConnectionContext context) {
		if (context != null) {
			context.writeAndFlush(MessageBuilder.closePortConnection());
		}
	}
}
