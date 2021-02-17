package com.celizion.kcg.ems.fm.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.celizion.kcg.ems.tta.util.excel.ExcelSheet2Class;
import com.celizion.kcg.ems.tta.util.excel.InvalidFormatException;
import com.celizion.kcg.ems.tta.util.excel.analysis.model.ColumnLineInfo;
import com.celizion.kcg.ems.tta.util.excel.analysis.model.SheetAnalysisModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigLoader {
	public <T> List<T> loadAlarmConfig(File configFile, int pageIndex, Class<?> modelClass,
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

	public List<ContelaAlarmConfig> loadContelaConfig() {
		return loadAlarmConfig(new File("./config/alarm/Contela_AlarmList_v2020.12.23.xlsx"), 0,
				ContelaAlarmConfig.class, (sheet, cls) -> {
					try {
						return loadDataOnSheet(sheet, cls, null);
					} catch (Exception e) {
						throw new RuntimeException(e.getClass().getName() + "::" + e.getMessage());
					}
				});
	}

	public List<ContelaAlarmConfig> loadCybertelConfig() {
		return loadAlarmConfig(new File("./config/alarm/Cybertel_AlarmList_v2021.01.08.xlsx"), 0,
				CybertelAlarmConfig.class, (sheet, cls) -> {
					try {
						return loadDataOnSheet(sheet, cls, null);
					} catch (Exception e) {
						throw new RuntimeException(e.getClass().getName() + "::" + e.getMessage());
					}
				});
	}

	public List<NokiaAlarmConfig> loadNokiaSPGWConfig() {
		return loadAlarmConfig(new File("./config/alarm/Nokia_AlarmList_v1.0.xlsx"), 0, NokiaAlarmConfig.class,
				(sheet, cls) -> {
					try {
						return loadDataOnSheet(sheet, cls, null);
					} catch (Exception e) {
						throw new RuntimeException(e.getClass().getName() + "::" + e.getMessage());
					}
				});
	}

	public List<NokiaAlarmConfig> loadNokiaMMEConfig() {
		return loadAlarmConfig(new File("./config/alarm/Nokia_AlarmList_v1.0.xlsx"), 1, NokiaAlarmConfig.class,
				(sheet, cls) -> {
					try {
						return loadDataOnSheet(sheet, cls, new ColumnLineInfo(1, 2));
					} catch (Exception e) {
						throw new RuntimeException(e.getClass().getName() + "::" + e.getMessage());
					}
				});
	}

	private <T> List<T> loadDataOnSheet(XSSFSheet sheet, Class<?> modelClass, ColumnLineInfo columnLineInfo)
			throws InvalidFormatException, InstantiationException, IllegalAccessException {
		SheetAnalysisModel analysisModel = ExcelSheet2Class.extractAnalysisModel(modelClass, columnLineInfo);
		List<T> alarmConfigs = ExcelSheet2Class.parseSheet(analysisModel, sheet);

		for (T alarmConfig : alarmConfigs) {
			System.out.println(alarmConfig);
		}
		return alarmConfigs;
	}
}
