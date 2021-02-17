package com.celizion.kcg.ems.tta.controller;

import io.netty.channel.Channel;

public interface ChannelElement {
	public Channel getChannel();

	public void close() throws InterruptedException;
}
