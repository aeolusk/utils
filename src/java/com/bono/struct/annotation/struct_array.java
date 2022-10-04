package com.bono.struct.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface struct_array {
	public int size() default 0;

	public String sizeField() default "N/A";

	public Class<?> cls() default Object.class;

	public String exist() default "";
}
