package com.celizion.kcg.ems.tta.controller;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.define.PType;

import io.netty.channel.Channel;

public class MessageDecoder implements MessageDefinition {
	private static Map<Channel, MessageDecoder> decoderPool = new HashMap<>();

	private MessageDecoder() {
		// do nothing.
	}

	public static synchronized void clear(Channel channel) {
		MessageDecoder decoder = decoderPool.remove(channel);

		if (decoder != null)
			decoder.clearParamData();
	}

	public static synchronized MessageDecoder get(Channel channel) {
		MessageDecoder decoder = decoderPool.get(channel);

		if (decoder == null) {
			decoder = new MessageDecoder();
			decoderPool.put(channel, decoder);
		}
		return decoder;
	}

	private int prevMsgId = -1;
	private int prevSeqNumber = -1;
	private int prevParamLength = 0;
	private ByteBuffer paramData = null;

	private void clearParamData() {
		prevMsgId = -1;
		prevParamLength = 0;
		if (paramData != null) {
			paramData.clear();
		}
		paramData = null;
	}

	private void reinitParamData(int msgId, int seqNumber, int length) {
		clearParamData();
		paramData = ByteBuffer.allocate(length);
		paramData.order(DEFAULT_BYTE_ORDER);
		prevSeqNumber = seqNumber;
		prevParamLength = length;
		prevMsgId = msgId;
	}

	private int getRemainParamLength(int length) {
		if (paramData == null)
			return length;
		return length - paramData.position();
	}

	private ByteBuffer getParamData() {
		paramData.position(0);
		return paramData;
	}

	private void appendParamData(int msgId, int seqNumber, int length, byte[] data) {
		if (prevMsgId != msgId || length != prevParamLength || paramData == null) {
			reinitParamData(msgId, seqNumber - 1, length);
		}
		paramData.put(data);

		if (prevSeqNumber + 1 != seqNumber)
			System.out.println("Invalid sequence number " + seqNumber + "(previous number is " + prevSeqNumber + ")");
		prevSeqNumber = seqNumber;
	}

	public synchronized Message decode(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		buf.order(DEFAULT_BYTE_ORDER);

		PType ptype = PType.find(buf.getInt());
		int msgId = buf.getInt();
		short segmentFlag = buf.getShort();
		short seqNumber = buf.getShort();
		int length = buf.getInt();
		int paramLength = getRemainParamLength(length);

		if (paramLength > MAX_PARAMETERS_LENGTH)
			paramLength = MAX_PARAMETERS_LENGTH;

		byte[] paramData = new byte[paramLength];
		buf.get(paramData);

		appendParamData(msgId, seqNumber, length, paramData);
		if (segmentFlag == SEGMENT_NONE_DIVIDE) {
			// Single packet or last packet of divide packets.
			Message message = new Message(ptype, msgId);
			message.decodeParameter(message, getParamData());
			clearParamData();
			return message;
		}
		return null;
	}

}
