package com.bono.util.excel;

@ColumnLineOfSheet(columnRowIndex = 1, startRowIndexOfData = 2)
public class AlarmConfig {

	@ColumnInfoOfSheet(name = "Alarm Name")
	public String alarmName;

	@ColumnInfoOfSheet(name = "Alarm Code")
	public String alarmCode;

	@ColumnInfoOfSheet(name = "Severity")
	public String severity;

	@ColumnInfoOfSheet(name = "Explanation")
	public String explanation;

	@ColumnInfoOfSheet(name = "Event Type")
	public String eventType;

	@ColumnInfoOfSheet(name = "Probable cause")
	public String probableCause;

	@Override
	public String toString() {
		return "AlarmConfig [alarmName=" + alarmName + ", alarmCode=" + alarmCode + ", severity=" + severity
				+ ", explanation=" + explanation + ", eventType=" + eventType + ", probableCause=" + probableCause
				+ "]";
	}
}
