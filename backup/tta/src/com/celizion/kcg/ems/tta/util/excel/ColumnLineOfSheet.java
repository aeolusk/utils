package com.celizion.kcg.ems.tta.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface ColumnLineOfSheet {
	int columnRowIndex();
	int startRowIndexOfData();
}
