package com.celizion.kcg.ems.tta.define;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PType implements ActionTimer, ActionRetryCount {
	AppStatusCheck(1, TIMER6, COUNT6),
	AppStatusCheckAck(2, NoneTimer, NoneCount),
	ClosePortConnection(3, TIMER7, COUNT7),
	ClosePortConnectionAck(4, NoneTimer, NoneCount),
	InputCommand(5, TIMER4, COUNT4, ActionParameter.InOutString),
	InputCommandAck(6, NoneTimer, NoneCount, ActionParameter.SyntaxCheckFlag, ActionParameter.InOutString),
	OutputMsg(7, NoneTimer, NoneCount, ActionParameter.InOutString),
	PortTypeConfirm(8, TIMER1, COUNT1, ActionParameter.PORTtype, ActionParameter.NEindex),
	PortTypeConfirmAck(9, NoneTimer, NoneCount),
	StartMsgTransmission(10, TIMER2, COUNT2, ActionParameter.MSGtype),
	StartMsgTransmissionAck(11, NoneTimer, NoneCount),
	StopMsgTransmission(12, TIMER3, COUNT3, ActionParameter.MSGtype),
	Control_ACCEPT(1000, 0, 0),
	Control_CLOSE_CLIENT(1001, 0, 0),
	Unknown(99, NoneTimer, NoneCount)
	;
	
	public final int code;
	
	public final int replyTimerLimit; // seconds
	public final int retryLimit; // count
	private final List<ActionParameter> params;
	
	private PType(int code, int timer, int retryCount, ActionParameter ... params) {
		this.code = code;
		this.replyTimerLimit = timer;
		this.retryLimit = retryCount;
		this.params = Collections.unmodifiableList(Arrays.asList(params));
	}

	public List<ActionParameter> getParamsDefinition() {
		return params;
	}
	
	public static PType find(int code) {
		for(PType ptype : PType.values()) {
			if(ptype.code == code)
				return ptype;
		}
		return Unknown;
	}
}
