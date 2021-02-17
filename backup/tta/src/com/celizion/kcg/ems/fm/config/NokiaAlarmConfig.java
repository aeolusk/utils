package com.celizion.kcg.ems.fm.config;

import com.celizion.kcg.ems.tta.util.excel.ColumnInfoOfSheet;
import com.celizion.kcg.ems.tta.util.excel.ColumnLineOfSheet;

@ColumnLineOfSheet(columnRowIndex = 0, startRowIndexOfData = 1)
public class NokiaAlarmConfig {
	
	@ColumnInfoOfSheet(name = "Alarm Name")
	public String alarmName;
	
	@ColumnInfoOfSheet(name = "Alarm ID")
	public String alarmID;
	
	@ColumnInfoOfSheet(name = "Alarm Type")
	public String alarmType;

	@ColumnInfoOfSheet(name = "Specific Problem")
	public String apecificProblem;
	
	@ColumnInfoOfSheet(name = "Default Severity")
	public String severity;
	
	@ColumnInfoOfSheet(name = "Implicitly Cleared")
	public String implicitlyCleared;
	
	@ColumnInfoOfSheet(name = "Default Probable Cause")
	public String probableCause;
	
	@ColumnInfoOfSheet(name = "Description")
	public String description;
	
	@ColumnInfoOfSheet(name = "Applicable Probable Causes")
	public String probableCauses;

	@Override
	public String toString() {
		return "NokiaAlarmConfig [alarmName=" + alarmName + ", alarmID=" + alarmID + ", alarmType=" + alarmType
				+ ", apecificProblem=" + apecificProblem + ", severity=" + severity + ", implicitlyCleared="
				+ implicitlyCleared + ", probableCause=" + probableCause + ", description=" + description
				+ ", probableCauses=" + probableCauses + "]";
	}
}
