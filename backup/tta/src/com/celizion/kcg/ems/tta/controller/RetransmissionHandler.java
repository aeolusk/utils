package com.celizion.kcg.ems.tta.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.celizion.kcg.ems.tta.Message;
import com.celizion.kcg.ems.tta.define.PType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetransmissionHandler {
	private int outputMsgCount = 0;
	private Timer timer;
	private Map<PType, RetransmissionInfo> transmissionInfo = new HashMap<>();
	private ConnectionContext context;

	class RetransmissionInfo {
		final int retryCount;
		long transmissionTime;
		final Message message;
		private boolean onTryRetransmission = false;

		public RetransmissionInfo(int retryCount, Message message) {
			this.retryCount = retryCount;
			this.message = message;
			this.transmissionTime = System.currentTimeMillis();
		}

		public void tryRetransmission() {
			this.onTryRetransmission = true;
		}

		public boolean onTryRetransmission() {
			return onTryRetransmission;
		}
	}

	public RetransmissionHandler(ConnectionContext context) {
		this.context = context;
		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					checkRetransmission();
				} catch (Exception e) {
					// do nothing.
				}
			}
		}, 1000, 1000);
	}

	public void clear() {
		if (timer != null)
			timer.cancel();
		timer = null;
		transmissionInfo.clear();
	}

	private void saveTransmissionInfo(Message message) {
		RetransmissionInfo info = transmissionInfo.remove(message.ptype);
		if (info == null) {
			transmissionInfo.put(message.ptype, new RetransmissionInfo(0, message));
			return;
		}

		transmissionInfo.put(message.ptype, new RetransmissionInfo(info.retryCount + 1, message));
	}

	private synchronized void checkRetransmission() {
		long currentTime = System.currentTimeMillis();
		boolean deleteStopMsgTransmission = false;

		for (RetransmissionInfo info : transmissionInfo.values()) {
			int timeLimit = info.message.ptype.replyTimerLimit * 1000;
			if (!info.onTryRetransmission() && currentTime - info.transmissionTime > timeLimit) {
				if (info.message.ptype == PType.StopMsgTransmission) {
					if (outputMsgCount == 0) {
						deleteStopMsgTransmission = true;
						continue;
					}
				}
				info.tryRetransmission();
				int retryLimit = info.message.ptype.retryLimit;
				if (info.retryCount >= retryLimit) {
					// Case when last retry packet sent.
					handleRetryFail(info.message);
				} else {
					log.debug("try retransmission ... retry count = " + info.retryCount + ", ptype = "
							+ info.message.ptype);
					context.notifyRetransmission(info.message);
				}
			}
		}
		if (deleteStopMsgTransmission)
			transmissionInfo.remove(PType.StopMsgTransmission);
	}

	private void handleRetryFail(Message message) {
		switch (message.ptype) {
		case PortTypeConfirm:
		case AppStatusCheck:
		case ClosePortConnection:
		case InputCommand:
		case StartMsgTransmission:
		case StopMsgTransmission:
			// TODO:: Fire NMS Alarm.
			log.warn("Client can't reply for " + message);
			break;
		default:
			// do nothing.
		}

	}

	public synchronized void doPostSend(Message message) {
		switch (message.ptype) {
		case PortTypeConfirm:
		case AppStatusCheck:
		case ClosePortConnection:
		case InputCommand:
		case StartMsgTransmission:
			saveTransmissionInfo(message);
			break;
		case StopMsgTransmission:
			outputMsgCount = 0;
			saveTransmissionInfo(message);
			break;
		default:
			// do nothing.
		}
	}

	public synchronized void doPostReply(Message message) {
		switch (message.ptype) {
		case PortTypeConfirmAck:
			transmissionInfo.remove(PType.PortTypeConfirm);
			break;
		case AppStatusCheckAck:
			transmissionInfo.remove(PType.AppStatusCheck);
			break;
		case ClosePortConnectionAck:
			transmissionInfo.remove(PType.ClosePortConnection);
			break;
		case InputCommandAck:
			transmissionInfo.remove(PType.InputCommand);
			break;
		case StartMsgTransmissionAck:
			transmissionInfo.remove(PType.StartMsgTransmission);
			break;
		case OutputMsg:
			outputMsgCount++;
			break;
		default:
			// do nothing.
		}
	}
}
