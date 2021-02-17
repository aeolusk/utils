package com.celizion.kcg.ems.tta.simulator;

import java.util.Arrays;
import java.util.List;

public enum PODItem {
	NETWORKELEMENT(""),
	LOCATION(""),
	EVENTTYPE(""),
	PROBABLECAUSE(""),
	SPECIFICPROBLEM(""),
	PERCEIVEDSEVERITY(""),
	ADDITIONALTEXT(""),

	GRADE(""),
	ADDITIONAL_INFO(""),
	OCCURRED_TIME("")
	// LOCATION        ONEBOXEPC00/SPGW00
	// GRADE           CRITICAL
	// EVENTTYPE       Communications
	// PROBABLECAUSE   Connection Establishment Error
	// ADDITIONALINFO TCP Connection Fail
	// OCCURRED TIME   2020/12/23 05:45:37
	// COMPLETED
	
	;

	public final String dbField;

	private PODItem(String dbField) {
		this.dbField = dbField;
	}
	
	public List<PODItem> getCybertelAlarmItems() {
		return Arrays.asList(NETWORKELEMENT, LOCATION, EVENTTYPE, PROBABLECAUSE, SPECIFICPROBLEM, PERCEIVEDSEVERITY, ADDITIONALTEXT);
	}
	
	public List<PODItem> getContelaAlarmItems() {
		return Arrays.asList(LOCATION, GRADE, EVENTTYPE, PROBABLECAUSE, ADDITIONAL_INFO, OCCURRED_TIME);
	}
}
