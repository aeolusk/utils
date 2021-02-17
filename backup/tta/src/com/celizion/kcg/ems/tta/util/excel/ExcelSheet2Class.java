package com.celizion.kcg.ems.tta.util.excel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.celizion.kcg.ems.tta.util.excel.analysis.model.ColumnAnalysisModel;
import com.celizion.kcg.ems.tta.util.excel.analysis.model.ColumnLineInfo;
import com.celizion.kcg.ems.tta.util.excel.analysis.model.SheetAnalysisModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelSheet2Class {
	public static SheetAnalysisModel extractAnalysisModel(Class<?> modelClass) {
		return extractAnalysisModel(modelClass, null);
	}

	public static SheetAnalysisModel extractAnalysisModel(Class<?> modelClass, ColumnLineInfo columnLineInfo) {
		SheetAnalysisModel model = null;
		if (columnLineInfo != null) {
			model = new SheetAnalysisModel(columnLineInfo.columnRowIndex, columnLineInfo.startRowIndexOfData,
					modelClass);
		} else {
			for (Annotation annotation : modelClass.getDeclaredAnnotations()) {
				if (annotation instanceof ColumnLineOfSheet) {
					ColumnLineOfSheet closAnnotation = (ColumnLineOfSheet) annotation;
					model = new SheetAnalysisModel(closAnnotation.columnRowIndex(),
							closAnnotation.startRowIndexOfData(), modelClass);
					break;
				}
			}
		}

		if (model != null) {
			Field[] fields = modelClass.getDeclaredFields();
			for (Field field : fields) {
				for (Annotation annotation : field.getDeclaredAnnotations()) {
					if (annotation instanceof ColumnInfoOfSheet) {
						ColumnInfoOfSheet columnInfo = (ColumnInfoOfSheet) annotation;
						model.addColumnAnalysisModel(field.getName(),
								new ColumnAnalysisModel(columnInfo.name(), columnInfo.index()));
					}
				}
			}
		}
		return model;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> parseSheet(SheetAnalysisModel analysisModel, XSSFSheet sheet)
			throws InvalidFormatException, InstantiationException, IllegalAccessException {
		List<T> values = new ArrayList<>();
		parseLineOfColumnNames(analysisModel, sheet);

		int rows = sheet.getPhysicalNumberOfRows();
		int startRowIndex = analysisModel.getStartRowIndexOfData();

		for (int rowindex = startRowIndex; rowindex < rows; rowindex++) {
			XSSFRow row = sheet.getRow(rowindex);
			if (row == null)
				continue;

			Class<?> modelClass = analysisModel.getModelClass();

			Object instance = modelClass.newInstance();
			Field[] fields = modelClass.getDeclaredFields();

			for (Field field : fields) {
				ColumnAnalysisModel columnAnalysisModel = analysisModel.getColumnAnalysisModels(field.getName());
				XSSFCell cell = row.getCell(columnAnalysisModel.getColumnIndex());

				if (field.getType().isAssignableFrom(String.class)) {
					field.set(instance, ExcelUtils.cell2String(cell));
				} else if (field.getType().isAssignableFrom(Double.class)) {
					field.set(instance, cell.getNumericCellValue());
				} else if (field.getType().isAssignableFrom(Long.class)) {
					field.set(instance, Double.valueOf(cell.getNumericCellValue()).longValue());
				} else if (field.getType().isAssignableFrom(Boolean.class)) {
					field.set(instance, cell.getBooleanCellValue());
				} else {
					log.warn("Can't find proper type to set for value [" + cell.getRawValue() + "]");
				}
			}
			values.add((T) instance);
		}
		return values;
	}

	private static void parseLineOfColumnNames(SheetAnalysisModel analysisModel, XSSFSheet sheet)
			throws InvalidFormatException {
		XSSFRow row = sheet.getRow(analysisModel.getColumnRowIndex());
		if (row == null)
			throw new InvalidFormatException(
					"Can't find row of column names. (row index=" + analysisModel.getColumnRowIndex() + ")");

		int cells = row.getPhysicalNumberOfCells();
		for (int columnIndex = 0; columnIndex <= cells; columnIndex++) {
			XSSFCell cell = row.getCell(columnIndex);
			if (cell == null) {
				log.warn("Can't find cell information for index (" + columnIndex + ")");
				continue;
			}

			String columnName = ExcelUtils.cell2String(cell).trim();
			analysisModel.setColumnIndex(columnName, columnIndex);
		}

		analysisModel.validate();
	}
}
