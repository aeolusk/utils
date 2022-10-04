package com.bono.struct;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.bono.struct.annotation.char_array;
import com.bono.struct.annotation.int32_t;
import com.bono.struct.annotation.struct_array;
import com.bono.struct.annotation.struct_t;
import com.bono.struct.annotation.uint32_t;
import com.bono.struct.annotation.uint8_t_array;
import com.bono.struct.exception.StructChangeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Class2Struct {
	public static final String NONE_SIZE_FIELD = "N/A";

	private ByteBuffer buf;

	public static ByteBuffer buildByteBuffer(int size) {
		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.order(ByteOrder.BIG_ENDIAN);
		return buf;
	}

	private Class2Struct(int size) {
		buf = Class2Struct.buildByteBuffer(size);
	}

	public static Class2Struct create(long size) {
		return new Class2Struct((int) size);
	}

	public byte[] makeStruct(Object data) throws Exception {
		return makeStructWithoutEncrypt(data);
	}

	private byte[] makeStructWithoutEncrypt(Object data) throws Exception {
		buf.position(0);

		try {
			_obj2struct_ex(data);
		} catch (Exception e) {
			if (data != null)
				log.debug("Can't fail to struct for " + data.getClass().getName());
			throw e;
		}

		if (buf.position() == buf.capacity()) {
			return buf.array();
		}

		byte[] content = new byte[buf.position()];
		buf.position(0);
		buf.get(content);
		return content;
	}

	private void _obj2struct(Object data) throws Exception {
		for (Field field : data.getClass().getFields()) {
			if (!existField(data, field))
				break;
			for (Annotation annotation : field.getAnnotations()) {
				switch (annotation.annotationType().getSimpleName()) {
				case "uint8_t":
					write_uint8_t(data, field);
					break;
				case "int8_t":
					write_int8_t(data, field);
					break;
				case "uint16_t":
					write_uint16_t(data, field);
					break;
				case "int16_t":
					write_int16_t(data, field);
					break;
				case "uint32_t":
					write_uint32_t(data, field);
					break;
				case "int32_t":
					write_int32_t(data, field);
					break;
				case "uint64_t":
					write_uint64_t(data, field);
					break;
				case "int64_t":
					write_int64_t(data, field);
					break;
				case "float_t":
					write_float_t(data, field);
					break;
				case "double_t":
					write_double_t(data, field);
					break;
				case "char_array":
					write_char_array(data, field);
					break;
				case "uint8_t_array":
					write_uint8_t_array(data, field);
					break;
				case "int8_t_array":
					write_int8_t_array(data, field);
					break;
				case "struct_t":
					write_struct_t(data, field);
					break;
				case "struct_array":
					write_struct_array(data, field);
					break;
				default:
					log.warn("Oops... find unknown annotation " + annotation.annotationType().getSimpleName());
				}
			}
		}
	}

	public static boolean existField(Object data, Field field) throws Exception {
		// Expression: [variable name] [eq|ne] [value]
		String existCondition = "";
		for (Annotation annotation : field.getAnnotations()) {
			switch (annotation.annotationType().getSimpleName()) {
			case "uint8_t":
				return true;
			case "int8_t":
				return true;
			case "uint16_t":
				return true;
			case "int16_t":
				return true;
			case "uint32_t":
				uint32_t _uint32 = field.getAnnotation(uint32_t.class);
				existCondition = _uint32.exist();
				break;
			case "int32_t":
				int32_t _int32 = field.getAnnotation(int32_t.class);
				existCondition = _int32.exist();
				break;
			case "uint64_t":
				return true;
			case "int64_t":
				return true;
			case "float_t":
				return true;
			case "double_t":
				return true;
			case "char_array":
				return true;
			case "uint8_t_array":
				return true;
			case "int8_t_array":
				return true;
			case "struct_t":
				struct_t _struct = field.getAnnotation(struct_t.class);
				existCondition = _struct.exist();
				break;
			case "struct_array":
				struct_array _struct_ary = field.getAnnotation(struct_array.class);
				existCondition = _struct_ary.exist();
				break;
			default:
				// do nothing.
			}
		}
		
		if ("".equals(existCondition.trim()))
			return true;

		String[] token = existCondition.split(" +");
		if (token.length != 3)
			return true; // invalid expression.

		String fieldValue = getFieldValue(data, token[0].trim());
		String compareValue = token[2];

		if ("eq".equals(token[1])) {
			return fieldValue.equals(compareValue);
		} else if ("ne".equals(token[1])) {
			return !fieldValue.equals(compareValue);
		}

		return true;
	}

	private void write_uint8_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.put((byte) field.getLong(obj));
	}

	private void write_int8_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.put((byte) field.getLong(obj));
	}

	private void write_uint16_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putShort((short) field.getLong(obj));
	}

	private void write_int16_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putShort((short) field.getLong(obj));
	}

	private void write_uint32_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putInt((int) field.getLong(obj));
	}

	private void write_int32_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putInt((int) field.getLong(obj));
	}

	private void write_uint64_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putLong(field.getLong(obj));
	}

	private void write_int64_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putLong(field.getLong(obj));
	}

	private void write_float_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putFloat(field.getFloat(obj));
	}

	private void write_double_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		buf.putDouble(field.getDouble(obj));
	}

	private void write_char_array(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException,
			StructChangeException, NoSuchFieldException, SecurityException {
		char_array fieldInf = field.getAnnotation(char_array.class);

		String value = (String) field.get(obj);
		byte[] outputData = value.getBytes();
		int outputSize = 0;

		if (fieldInf.size() > 0) {
			outputSize = fieldInf.size();
		} else if (!fieldInf.sizeField().equals(NONE_SIZE_FIELD)) {
			outputSize = getSizeFieldValue(obj, fieldInf.sizeField());
			if (outputSize <= 0)
				throw new StructChangeException("uint8_t_array:unknown size field [" + fieldInf.sizeField() + "]");
		} else
			throw new StructChangeException("uint8_t_array:unknown size");

		writeByteArray(outputData, outputSize);
	}

	private void write_uint8_t_array(Object obj, Field field) throws StructChangeException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		uint8_t_array fieldInf = field.getAnnotation(uint8_t_array.class);

		byte[] outputData = (byte[]) field.get(obj);
		int outputSize = 0;

		if (fieldInf.size() > 0) {
			outputSize = fieldInf.size();
		} else if (!fieldInf.sizeField().equals(NONE_SIZE_FIELD)) {
			outputSize = getSizeFieldValue(obj, fieldInf.sizeField());
			if (outputSize <= 0)
				throw new StructChangeException("uint8_t_array:unknown size field [" + fieldInf.sizeField() + "]");
		} else
			throw new StructChangeException("uint8_t_array:unknown size");

		writeByteArray(outputData, outputSize);
	}

	private void writeByteArray(byte[] outputData, int outputSize) {
		int position = buf.position();

		if (outputData == null) {
			buf.position(position + outputSize);
			return;
		}

		if (outputSize > outputData.length) {
			buf.put(outputData);
		} else {
			buf.put(outputData, 0, outputSize);
		}
		buf.position(position + outputSize);
	}

	private void write_int8_t_array(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, StructChangeException {
		write_uint8_t_array(obj, field);
	}

	private void write_struct_t(Object obj, Field field) throws Exception {
		Object value = field.get(obj);
		_obj2struct_ex(value);
	}

	private void write_struct_array(Object obj, Field field) throws Exception {
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) field.get(obj);
		for (Object value : values) {
			_obj2struct_ex(value);
		}
	}

	private void _obj2struct_ex(Object value) throws Exception {
		if (value instanceof SpecificCodecImpl) {
			((SpecificCodecImpl) value).toWire(buf);
		} else {
			_obj2struct(value);
		}
	}

	public static int getSizeFieldValue(Object obj, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (NONE_SIZE_FIELD.equals(fieldName))
			return 0;

		Field field = obj.getClass().getField(fieldName);
		if (field != null && field.getType().isPrimitive()) {
			if ("long".equals(field.getType().getSimpleName()))
				return (int) field.getLong(obj);
			else if ("int".equals(field.getType().getSimpleName()))
				return (int) field.getInt(obj);
		}

		return 0;
	}

	public static String getFieldValue(Object obj, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = obj.getClass().getField(fieldName);
		if (field != null) {
			return "" + field.get(obj);
		}
		return null;
	}
}
