package com.bono.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bono.util.excel.model.ColumnLineInfo;
import com.bono.util.excel.model.SheetAnalysisModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelLoader {
	public static <T> List<T> load(File configFile, int pageIndex, Class<?> modelClass,
			BiFunction<XSSFSheet, Class<?>, List<T>> analysisFunction) {
		XSSFWorkbook workbook = null;

		try {
			FileInputStream inStream = new FileInputStream(configFile);
			workbook = new XSSFWorkbook(inStream);
			return analysisFunction.apply(workbook.getSheetAt(pageIndex), modelClass);
		} catch (Exception e) {
			log.error("{}", e);
		} finally {
			if (workbook != null)
				try {
					workbook.close();
				} catch (IOException e) {
					// do nothing.
				}
		}
		return null;
	}
	
	public static <T> List<T> loadDataOnSheet(XSSFSheet sheet, Class<?> modelClass, ColumnLineInfo columnLineInfo)
			throws InvalidFormatException, InstantiationException, IllegalAccessException {
		SheetAnalysisModel analysisModel = ExcelSheet2Class.extractAnalysisModel(modelClass, columnLineInfo);
		List<T> alarmConfigs = ExcelSheet2Class.parseSheet(analysisModel, sheet);

		for (T alarmConfig : alarmConfigs) {
			System.out.println(alarmConfig);
		}
		return alarmConfigs;
	}
	
}
