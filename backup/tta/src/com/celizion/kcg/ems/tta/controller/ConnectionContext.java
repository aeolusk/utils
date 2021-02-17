package com.celizion.kcg.ems.tta.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.define.PType;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionContext {
	private final ConnectionContext parentContext;

	private final Channel ch;
	public final int port;
	public final int NEIndex;
	private Map<String, Object> userData = new HashMap<>();

	private RetransmissionHandler retransmissionHandler = new RetransmissionHandler(this);

	public ConnectionContext(Channel ch, int port, int neIndex) {
		this(null, ch, port, neIndex);
	}

	public ConnectionContext(ConnectionContext parentContext, Channel ch, int port, int neIndex) {
		this.parentContext = parentContext;
		this.ch = ch;
		this.port = port;
		this.NEIndex = neIndex;
	}

	public ConnectionContext getParentContext() {
		return parentContext;
	}

	public void writeAndFlush(Message message) {
		if (message.ptype == PType.OutputMsg && enableTransmission == false) {
			log.debug("Ignore write message because disabled transmission flag.(" + message + ")");
			return;
		}
		log.debug("write message(" + message + ").");
		ch.writeAndFlush(message);
		retransmissionHandler.doPostSend(message);
	}

	private boolean enableTransmission = false; // enabled after received StartMsgTransmissionAck.

	public void enableTransmission() {
		log.debug("enableTransmission. port=" + port);
		this.enableTransmission = true;
	}

	public void stopMessageTransmission() {
		this.enableTransmission = false;
	}

	private List<BiConsumer<ConnectionContext, Message>> consumerList = new CopyOnWriteArrayList<>();

	public void clear() {
		if (parentContext != null) {
			for (BiConsumer<ConnectionContext, Message> consumer : consumerList) {
				parentContext.removeMessageHandler(consumer);
			}
		}

		consumerList.clear();
		retransmissionHandler.clear();
		userData.clear();
		stopClientStatusCheck();
	}

	public void addMessageHandler(BiConsumer<ConnectionContext, Message> consumer) {
		consumerList.add(consumer);

		if (parentContext != null)
			parentContext.addMessageHandler(consumer);
	}

	public void removeMessageHandler(BiConsumer<ConnectionContext, Message> consumer) {
		consumerList.remove(consumer);

		if (parentContext != null)
			parentContext.removeMessageHandler(consumer);
	}

	public boolean handleMessage(Message msg) throws Exception {
		if (consumerList.size() == 0)
			return false;

		try {
			for (BiConsumer<ConnectionContext, Message> consumer : consumerList) {
				consumer.accept(this, msg);
			}
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public void notifyRetransmission(Message msg) {
		writeAndFlush(msg.makeRetransmissionMessage());
	}

	public void doPostReply(Message msg) {
		retransmissionHandler.doPostReply(msg);
	}

	public void addUserData(String key, Object value) {
		userData.put(key, value);
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}

	private Timer timer;

	public void startClientStatusCheck() {
		if (timer != null)
			return;

		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					writeAndFlush(MessageBuilder.appStatusCheck());
				} catch (Exception e) {
					// do nothing.
				}
			}
		}, 1000, PType.TIMER5 * 1000);

	}

	private void stopClientStatusCheck() {
		if (timer != null)
			timer.cancel();
		timer = null;
	}
}
