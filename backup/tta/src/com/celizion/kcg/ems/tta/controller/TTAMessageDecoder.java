package com.celizion.kcg.ems.tta.controller;

import java.nio.ByteOrder;
import java.util.List;

import com.celizion.kcg.ems.tta.define.HeaderStructure;
import com.celizion.kcg.ems.tta.util.ByteUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TTAMessageDecoder extends ByteToMessageDecoder implements MessageDefinition {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		log.debug("readableBytes = " + in.readableBytes());
		if (in.readableBytes() < HeaderStructure.getLength()) {
			return;
		}

		int length = getMessageLength(in);
		int packetCount = length / MAX_PARAMETERS_LENGTH + (length % MAX_PARAMETERS_LENGTH == 0 ? 0 : 1);

		if (length == 0)
			packetCount = 1; // only exist header information.

		if (in.readableBytes() < HeaderStructure.getLength() * packetCount + length) {
			log.debug("readable byte=" + +in.readableBytes() + ", expected length="
					+ (HeaderStructure.getLength() * packetCount + length));
			return;
		}

		int remainSize = HeaderStructure.getLength() * packetCount + length;
		while (remainSize > 0) {
			byte[] buf = null;
			if (remainSize > MAX_PACKET_LENGTH) {
				buf = new byte[MAX_PACKET_LENGTH];
			} else {
				buf = new byte[remainSize];
			}
			in.readBytes(buf);
			out.add(buf);
			ByteUtils.traceSimple(buf);
			remainSize -= MAX_PACKET_LENGTH;
		}
	}

	private int getMessageLength(ByteBuf in) {
		if (DEFAULT_BYTE_ORDER == ByteOrder.LITTLE_ENDIAN)
			return in.getIntLE(HeaderStructure.Length.offset);
		return in.getInt(HeaderStructure.Length.offset);
	}
}
