package com.celizion.kcg.ems.tta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.celizion.kcg.ems.tta.MessageBuilder;
import com.celizion.kcg.ems.tta.controller.handler.TTAProtocolHandler;
import com.celizion.kcg.ems.tta.define.PType;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ConnectionManager {
	instance;

	private static AtomicInteger nextNEIndex = new AtomicInteger(0);

	private Map<Integer, ConnectionContext> parentContexts = new HashMap<>();
	private Map<Channel, ConnectionContext> connectionInfos = new HashMap<>();

	public synchronized ConnectionContext registerServer(final ChannelElement parentCH, int port) {
		ConnectionContext parentContext = parentContexts.get(port);
		if (parentContext == null) {
			// register parentContext
			parentContext = new ConnectionContext(parentCH.getChannel(), port, 0);
			parentContexts.put(port, parentContext);
		}
		return parentContext;
	}

	public synchronized ConnectionContext findServer(int port) {
		return parentContexts.get(port);
	}

	public synchronized List<ConnectionContext> findConnectedClient(int port) {
		List<ConnectionContext> clients = new ArrayList<>(); // client(NMS Part) list.
		for (Entry<Channel, ConnectionContext> entry : connectionInfos.entrySet()) {
			ConnectionContext parentContext = entry.getValue().getParentContext();

			if (parentContext != null && parentContext.port == port)
				clients.add(entry.getValue());
		}
		return clients;
	}

	public synchronized void connected(final ChannelElement parentCH, Channel ch, int port) {
		ConnectionContext context = null;
		if (parentCH != null) {
			context = new ConnectionContext(registerServer(parentCH, port), ch, port, nextNEIndex.addAndGet(1));
		} else {
			context = new ConnectionContext(ch, port, nextNEIndex.addAndGet(1));
		}

		MessageDecoder.clear(ch);
		connectionInfos.put(ch, context);
	}

	public void disconnected(Channel ch) {
		ConnectionContext context = connectionInfos.remove(ch);
		if (context != null)
			context.clear();
		MessageDecoder.clear(ch);
	}

	public List<ConnectionContext> getConnectedContexts() {
		return new ArrayList<ConnectionContext>(connectionInfos.values());
	}

	public ConnectionContext find(Channel ch) {
		return connectionInfos.get(ch);
	}

	public void broadcastClosePortConnection() {
		for (ConnectionContext context : connectionInfos.values()) {
			TTAProtocolHandler.sendClosePortConnection(context);
		}
	}

	public void handleClosePortConnectionAck(ConnectionContext context) {
		try {
			context.handleMessage(MessageBuilder.simple(PType.Control_CLOSE_CLIENT));
		} catch (Exception e) {
			log.error("{}", e);
		}
	}
}
