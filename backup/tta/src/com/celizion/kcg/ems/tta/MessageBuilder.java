package com.celizion.kcg.ems.tta;

import java.util.concurrent.atomic.AtomicInteger;

import com.celizion.kcg.ems.tta.define.ActionParameter;
import com.celizion.kcg.ems.tta.define.PType;

public class MessageBuilder {
	private static AtomicInteger nextMessageId = new AtomicInteger(0);

	public static int getMessageId() {
		return nextMessageId.addAndGet(1);
	}

	public static Message control(PType ptype) {
		return new Message(ptype, -1);
	}

	public static Message portTypeConfirm(byte PORTType, int neIndex) {
		Message msg = new Message(PType.PortTypeConfirm, nextMessageId.addAndGet(1));

		msg.addParameter(ActionParameter.PORTtype, PORTType);
		msg.addParameter(ActionParameter.NEindex, neIndex);
		return msg;
	}

	public static Message simple(PType ptype) {
		return new Message(ptype, nextMessageId.addAndGet(1));
	}

	public static Message portTypeConfirmAck() {
		return simple(PType.PortTypeConfirmAck);
	}

	public static Message startMessageTransmission(byte msgType) {
		Message msg = new Message(PType.StartMsgTransmission, nextMessageId.addAndGet(1));

		msg.addParameter(ActionParameter.MSGtype, msgType);
		return msg;
	}

	public static Message startMsgTransmissionAck() {
		return simple(PType.StartMsgTransmissionAck);
	}

	public static Message stopMessageTransmission(byte msgType) {
		Message msg = new Message(PType.StopMsgTransmission, nextMessageId.addAndGet(1));

		msg.addParameter(ActionParameter.MSGtype, msgType);
		return msg;
	}

	public static Message inputCommand(String command) {
		Message msg = new Message(PType.InputCommand, nextMessageId.addAndGet(1));

		msg.addParameter(ActionParameter.InOutString, command);
		return msg;
	}

	public static Message inputCommandAck(int reqMsgId, boolean isSyntaxValid, String command) {
		byte syntaxCheckFlag = (isSyntaxValid ? (byte) 0x01 : (byte) 0x02);
		Message msg = new Message(PType.InputCommandAck, reqMsgId);

		msg.addParameter(ActionParameter.SyntaxCheckFlag, syntaxCheckFlag);
		msg.addParameter(ActionParameter.InOutString, command);
		return msg;
	}

	public static Message outputMsg(int reqMsgId, String output) {
		Message msg = new Message(PType.OutputMsg, reqMsgId);

		msg.addParameter(ActionParameter.InOutString, output);
		return msg;
	}
	
	public static Message outputMsg(String output) {
		Message msg = new Message(PType.OutputMsg, nextMessageId.addAndGet(1));

		msg.addParameter(ActionParameter.InOutString, output);
		return msg;
	}

	public static Message appStatusCheck() {
		return simple(PType.AppStatusCheck);
	}

	public static Message appStatusCheckAck() {
		return simple(PType.AppStatusCheckAck);
	}

	public static Message closePortConnection() {
		return simple(PType.ClosePortConnection);
	}

	public static Message closePortConnectionAck() {
		return simple(PType.ClosePortConnectionAck);
	}

}
