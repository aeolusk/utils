package com.celizion.kcg.ems.fm.config;

import com.celizion.kcg.ems.tta.util.excel.ColumnInfoOfSheet;
import com.celizion.kcg.ems.tta.util.excel.ColumnLineOfSheet;

@ColumnLineOfSheet(columnRowIndex = 1, startRowIndexOfData = 2)
public class CybertelAlarmConfig {

	@ColumnInfoOfSheet(name = "�˶��ڵ�")
	public String alarmCode;

	@ColumnInfoOfSheet(name = "�˶���")
	public String alarmName;

	@ColumnInfoOfSheet(name = "SEVERITY")
	public String severity;

	@ColumnInfoOfSheet(name = "PROBABLE CAUSE")
	public String probableCause;

	@ColumnInfoOfSheet(name = "Specific Problem")
	public String specificProblem;

	@ColumnInfoOfSheet(name = "ALARM\nLOCATION")
	public String alarmLocation;

	@ColumnInfoOfSheet(name = "Threshold")
	public String threshold;

	@ColumnInfoOfSheet(name = "�߻�����")
	public String cause;

	@Override
	public String toString() {
		return "CybertelAlarmConfig [alarmCode=" + alarmCode + ", alarmName=" + alarmName + ", severity=" + severity
				+ ", probableCause=" + probableCause + ", specificProblem=" + specificProblem + ", alarmLocation="
				+ alarmLocation + ", threshold=" + threshold + ", cause=" + cause + "]";
	}

}
