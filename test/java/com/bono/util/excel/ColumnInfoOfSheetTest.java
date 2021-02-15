package com.bono.util.excel;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ColumnInfoOfSheetTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testLoad() {
		ExcelLoader.load(new File(ColumnInfoOfSheetTest.class.getResource("AlarmDefine.xlsx").getPath()), 0,
				AlarmConfig.class, (sheet, cls) -> {
					try {
						return ExcelLoader.loadDataOnSheet(sheet, cls, null);
					} catch (Exception e) {
						throw new RuntimeException(e.getClass().getName() + "::" + e.getMessage());
					}
				});		
	}
}
