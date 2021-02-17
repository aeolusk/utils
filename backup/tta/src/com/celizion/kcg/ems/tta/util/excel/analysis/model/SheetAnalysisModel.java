package com.celizion.kcg.ems.tta.util.excel.analysis.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.celizion.kcg.ems.tta.util.excel.InvalidFormatException;

public class SheetAnalysisModel {
	private int columnRowIndex;
	private int startRowIndexOfData;
	private Class<?> modelClass;

	private Map<String, ColumnAnalysisModel> columnAnalysisModels = new LinkedHashMap<>();

	public SheetAnalysisModel(int columnRowIndex, int startRowIndexOfData, Class<?> modelClass) {
		this.columnRowIndex = columnRowIndex;
		this.startRowIndexOfData = startRowIndexOfData;
		this.modelClass = modelClass;
	}

	public synchronized void addColumnAnalysisModel(String fieldName, ColumnAnalysisModel model) {
		columnAnalysisModels.put(fieldName, model);
	}

	public int getColumnRowIndex() {
		return columnRowIndex;
	}

	public int getStartRowIndexOfData() {
		return startRowIndexOfData;
	}

	public ColumnAnalysisModel getColumnAnalysisModels(String fieldName) {
		return columnAnalysisModels.get(fieldName);
	}

	public void setColumnIndex(String columnName, int index) {
		for (ColumnAnalysisModel analysisModel : columnAnalysisModels.values()) {
			if (analysisModel.getColumnIndex() == -1 && columnName.equals(analysisModel.getColumnName())) {
				analysisModel.setColumnIndex(index);
				break;
			}
		}
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public void validate() throws InvalidFormatException {
		for (ColumnAnalysisModel analysisModel : columnAnalysisModels.values()) {
			if (analysisModel.getColumnIndex() == -1)
				throw new InvalidFormatException("Can't find column name [" + analysisModel.getColumnName() + "]");
		}
	}
}
