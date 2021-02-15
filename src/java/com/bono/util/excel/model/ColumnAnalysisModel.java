package com.bono.util.excel.model;

public class ColumnAnalysisModel {
	private String columnName;
	private int columnIndex;

	public ColumnAnalysisModel(String columnName, int columnIndex) {
		this.columnName = columnName;
		this.columnIndex = columnIndex;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getColumnName() {
		return columnName;
	}

}
