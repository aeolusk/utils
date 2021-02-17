package com.celizion.kcg.ems.tta.util.excel.analysis.model;

public class ColumnLineInfo {
	public final int columnRowIndex;
	public final int startRowIndexOfData;
	
	public ColumnLineInfo(int columnRowIndex, int startRowIndexOfData) {
		this.columnRowIndex = columnRowIndex;
		this.startRowIndexOfData = startRowIndexOfData;
	}
}
