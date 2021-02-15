package com.bono.util.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class ExcelUtils {
	public static String cell2String(XSSFCell cell) {
		String value = "";
		
		switch (cell.getCellType()) {
		case FORMULA:
			value = cell.getCellFormula();
			break;
		case NUMERIC:
			value = cell.getRawValue() + ""; // cell.getNumericCellValue()
			break;
		case STRING:
			value = cell.getStringCellValue() + "";
			break;
		case BOOLEAN:
			value = cell.getBooleanCellValue() + "";
			break;
		case BLANK:
			value = "";
			break;
		case ERROR:
			value = cell.getErrorCellValue() + "";
			break;
		case _NONE:
			// Unknown type
			break;
		}
		return value;
	}
}
