package com.celizion.kcg.ems.tta.controller;

import com.celizion.kcg.ems.tta.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TTAMessageOutboundHandler extends ChannelOutboundHandlerAdapter {
	@Override
	public void write(ChannelHandlerContext ctx, Object data, ChannelPromise promise) {
		if (data instanceof Message) {
			Message msg = (Message) data;

			for (byte[] packet : MessageEncoder.encode(msg)) {
				ByteBuf encoded = ctx.alloc().buffer(packet.length);
				encoded.writeBytes(packet);
				ctx.write(encoded, promise);
				// Can't call 'encoded.release()' because example code(in NETTY documentation)
				// isn't include.
			}
		} else {
			log.warn("Class only handles com.celizion.kcgepc.tta.Message instance.(not " + data.getClass().getName()
					+ ")");
		}
	}
}
