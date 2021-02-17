package com.celizion.kcg.ems.tta.controller;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.controller.handler.TTAProtocolHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TTAMessageInBoundHandler extends ChannelInboundHandlerAdapter {
	public final Channel channel;

	public TTAMessageInBoundHandler(Channel ch) {
		this.channel = ch;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		MessageDecoder.clear(channel);
		super.channelActive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof byte[]) {
			Message decodedMessage = MessageDecoder.get(channel).decode((byte[]) msg);
			if (decodedMessage != null) {
				log.debug(decodedMessage.toString());
				TTAProtocolHandler.handleMessage(channel, decodedMessage);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
