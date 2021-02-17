package com.celizion.kcg.ems.tta.simulator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PODData {
	private Map<PODItem, String> values = new ConcurrentHashMap<>();

	public PODData add(PODItem item, String value) {
		values.put(item, value);
		return this;
	}
	
	public String buildPOD() {
		return null;
	}
}
