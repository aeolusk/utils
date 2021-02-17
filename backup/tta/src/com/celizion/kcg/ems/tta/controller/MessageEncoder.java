package com.celizion.kcg.ems.tta.controller;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.define.PType;

public class MessageEncoder implements MessageDefinition {
	
	public static List<byte[]> encode(Message message) {
		return encode(message.ptype, message.messageId, message.encodeParameters());
	}
	
	public static List<byte[]> encode(PType ptype, int msgId, byte[] data) {
		List<byte[]> encodePackets = new ArrayList<>();
		int remainingLength = data.length;
		int dataOffset = 0;
		int parameterLength = 0;
		short segmentSequence = 0;
		short segmentFlag = SEGMENT_NONE_DIVIDE;

		while (true) {
			ByteBuffer buf = null;
			if (remainingLength + HEADER_PREFIX_LENGTH > MAX_PACKET_LENGTH) {
				buf = ByteBuffer.allocate(MAX_PACKET_LENGTH);
				buf.order(DEFAULT_BYTE_ORDER);
				
				segmentSequence++;
				segmentFlag = SEGMENT_DIVIDE;
				parameterLength = MAX_PACKET_LENGTH - HEADER_PREFIX_LENGTH;
				remainingLength -= parameterLength;

			} else {
				buf = ByteBuffer.allocate(HEADER_PREFIX_LENGTH + remainingLength);
				buf.order(DEFAULT_BYTE_ORDER);

				if (segmentSequence > 0)
					segmentSequence++; // for divided packets.
				segmentFlag = SEGMENT_NONE_DIVIDE;
				parameterLength = remainingLength;
			}
			buf.putInt(ptype.code);
			buf.putInt(msgId);
			buf.putShort(segmentFlag);
			buf.putShort(segmentSequence);
			buf.putInt(data.length);
			buf.put(data, dataOffset, parameterLength);

			encodePackets.add(buf.array());

			dataOffset += parameterLength;

			if (segmentFlag == SEGMENT_NONE_DIVIDE) // after encode last packet.
				break;
		}

		return encodePackets;
	}
}
