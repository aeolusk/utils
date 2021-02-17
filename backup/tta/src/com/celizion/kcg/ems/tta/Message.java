package com.celizion.kcg.ems.tta;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.celizion.kcg.ems.tta.controller.MessageDefinition;
import com.celizion.kcg.ems.tta.define.ActionParameter;
import com.celizion.kcg.ems.tta.define.PType;
import com.celizion.kcg.ems.tta.util.StringUtils;

public class Message implements MessageDefinition {
	public final PType ptype;
	public final int messageId;
	private Map<ActionParameter, Object> parameters = new LinkedHashMap<>();

	public Message(PType ptype, int messageId) {
		this.ptype = ptype;
		this.messageId = messageId;
	}

	public Message makeRetransmissionMessage() {
		Message msg = new Message(ptype, MessageBuilder.getMessageId());
		msg.parameters.putAll(parameters);
		return msg;
	}

	public void addParameter(ActionParameter paramType, Object value) {
		parameters.put(paramType, value);
		length = -1;
	}

	public Object getParameter(ActionParameter paramType) {
		return parameters.get(paramType);
	}

	private int length = -1;

	public int getLength() {
		if (length == -1)
			length = getParameterEncodeSize();
		return length;
	}

	public int getParameterEncodeSize() {
		int size = 0;

		for (Entry<ActionParameter, Object> entry : parameters.entrySet()) {
			switch (entry.getKey()) {
			case InOutString:
				size += entry.getValue().toString().getBytes().length;
				break;
			case SyntaxCheckFlag:
				size += 1;
				break;
			case PORTtype:
				size += 1;
				break;
			case NEindex:
				// TODO: SPEC isn't define.
				break;
			case MSGtype:
				size += 1;
				break;
			}
		}
		return size;
	}

	public byte[] encodeParameters() {
		ByteBuffer buf = ByteBuffer.allocate(getParameterEncodeSize());
		buf.order(DEFAULT_BYTE_ORDER);

		for (Entry<ActionParameter, Object> entry : parameters.entrySet()) {
			switch (entry.getKey()) {
			case InOutString:
				buf.put(entry.getValue().toString().getBytes());
				break;
			case SyntaxCheckFlag:
				buf.put(((Byte) entry.getValue()).byteValue());
				break;
			case PORTtype:
				buf.put(((Byte) entry.getValue()).byteValue());
				break;
			case NEindex:
				// TODO: SPEC isn't define.
				break;
			case MSGtype:
				buf.put(((Byte) entry.getValue()).byteValue());
				break;
			}
		}
		buf.position(0);
		return buf.array();
	}

	public void decodeParameter(Message message, ByteBuffer paramData) {
		List<ActionParameter> paramsDef = message.ptype.getParamsDefinition();

		for (ActionParameter paramType : paramsDef) {
			switch (paramType) {
			case InOutString:
				message.addParameter(paramType, extractInOutString(paramData));
				break;
			case SyntaxCheckFlag:
				message.addParameter(paramType, paramData.get());
				break;
			case PORTtype:
				message.addParameter(paramType, paramData.get());
				break;
			case NEindex:
				// TODO: SPEC isn't define.
				break;
			case MSGtype:
				message.addParameter(paramType, paramData.get());
				break;
			}
		}
	}

	private String extractInOutString(ByteBuffer paramData) {
		String value = new String(paramData.array(), paramData.position(), paramData.capacity() - paramData.position());
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Message [ptype=" + ptype + ", messageId=" + messageId + ", length=" + getLength() + "], Parameters=[");
		for (Entry<ActionParameter, Object> entry : parameters.entrySet()) {
			String value = StringUtils.toSingleLine(entry.getValue().toString());
			if (entry.getValue() instanceof String && value.length() > 80) {
				sb.append(entry.getKey() + "=[length=" + value.length() + "]"
						+ StringUtils.abbreviate(65, 10, value.toString()) + "");
			} else
				sb.append(entry.getKey() + "=" + value.toString() + "");
		}
		sb.append("]");
		return sb.toString();
	}
}
