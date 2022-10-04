package com.bono.struct;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.bono.struct.annotation.char_array;
import com.bono.struct.annotation.struct_array;
import com.bono.struct.annotation.struct_t;
import com.bono.struct.annotation.uint8_t_array;
import com.bono.struct.exception.StructChangeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Struct2Class<T> {
	private ByteBuffer buf;

	private Struct2Class(byte[] structData) {
		buf = Class2Struct.buildByteBuffer(structData.length);
		buf.put(structData);
		buf.position(0);
	}

	private Struct2Class(ByteBuffer buf, boolean resetBuf) {
		this.buf = buf;
		if (resetBuf)
			this.buf.position(0);
	}

	public static <T> Struct2Class<T> allocate(ByteBuffer buf) {
		return new Struct2Class<T>(buf, true);
	}

	public static <T> Struct2Class<T> allocate(ByteBuffer buf, boolean resetBuf) {
		return new Struct2Class<T>(buf, resetBuf);
	}

	public static <T> Struct2Class<T> allocate(byte[] data) {
		return new Struct2Class<T>(data);
	}

	public T makeClass(Class<T> cls) throws Exception {
		if (cls == null)
			return null;

		T obj = cls.newInstance();

		_struct2obj_ex(obj);
		return obj;
	}

	public int getPosition() {
		return buf.position();
	}

	private void _struct2obj(Object data, Class<?> cls) throws Exception {
		for (Field field : cls.getFields()) {
			if (buf.position() == buf.capacity())
				break;
			if (!Class2Struct.existField(data, field))
				break;
			for (Annotation annotation : field.getAnnotations()) {
				switch (annotation.annotationType().getSimpleName()) {
				case "uint8_t":
					read_uint8_t(data, field);
					break;
				case "int8_t":
					read_int8_t(data, field);
					break;
				case "uint16_t":
					read_uint16_t(data, field);
					break;
				case "int16_t":
					read_int16_t(data, field);
					break;
				case "uint32_t":
					read_uint32_t(data, field);
					break;
				case "int32_t":
					read_int32_t(data, field);
					break;
				case "uint64_t":
					read_uint64_t(data, field);
					break;
				case "int64_t":
					read_int64_t(data, field);
					break;
				case "float_t":
					read_float_t(data, field);
					break;
				case "double_t":
					read_double_t(data, field);
					break;
				case "char_array":
					read_char_array(data, field);
					break;
				case "uint8_t_array":
					read_uint8_t_array(data, field);
					break;
				case "int8_t_array":
					read_int8_t_array(data, field);
					break;
				case "struct_t":
					read_struct_t(data, field);
					break;
				case "struct_array":
					read_struct_array(data, field);
					break;
				default:
					log.warn("Oops... find unknown annotation " + annotation.annotationType().getSimpleName());
				}
			}
		}
	}

	public void read_uint8_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, buf.get());
	}

	public void read_int8_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, buf.get());
	}

	public void read_uint16_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, (long) buf.getShort());
	}

	public void read_int16_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, (long) buf.getShort());
	}

	public void read_uint32_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		if (buf.position() + 4 > buf.capacity()) {
			field.set(obj, (long) 0L);
			buf.position(buf.capacity());
			return;
		}
		field.set(obj, (long) (0xffffffff & buf.getInt()));
	}

	public void read_int32_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		if (buf.position() + 4 > buf.capacity()) {
			field.set(obj, (long) 0L);
			buf.position(buf.capacity());
			return;
		}
		field.set(obj, (long) buf.getInt());
	}

	public void read_uint64_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, (long) buf.getLong());
	}

	public void read_int64_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, (long) buf.getLong());
	}

	public void read_float_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, buf.getFloat());
	}

	public void read_double_t(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
		field.set(obj, buf.getDouble());
	}

	public void read_char_array(Object obj, Field field) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, StructChangeException {
		char_array fieldInf = field.getAnnotation(char_array.class);

		int length = 0;

		if (fieldInf.size() > 0) {
			length = fieldInf.size();
		} else if (!fieldInf.sizeField().equals(Class2Struct.NONE_SIZE_FIELD)) {
			length = getSizeFieldValue(obj, fieldInf.sizeField());
			if (length <= 0)
				throw new StructChangeException("uint8_t_array:unknown size field [" + fieldInf.sizeField() + "]");
		} else
			throw new StructChangeException("uint8_t_array:unknown size");

		byte[] data = new byte[length];
		buf.get(data);
		field.set(obj, new String(data, 0, getStringLength(data)));
	}

	private int getStringLength(byte[] data) {
		int strLength = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0)
				break;
			strLength++;
		}
		return strLength;
	}

	public void read_uint8_t_array(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, StructChangeException {
		uint8_t_array fieldInf = field.getAnnotation(uint8_t_array.class);

		int outputSize = 0;

		if (fieldInf.size() > 0) {
			outputSize = fieldInf.size();
		} else if (!fieldInf.sizeField().equals(Class2Struct.NONE_SIZE_FIELD)) {
			outputSize = getSizeFieldValue(obj, fieldInf.sizeField());
			if (outputSize <= 0)
				throw new StructChangeException("uint8_t_array:unknown size field [" + fieldInf.sizeField() + "]");
		} else
			throw new StructChangeException("uint8_t_array:unknown size");

		byte[] value = new byte[outputSize];
		buf.get(value);
		field.set(obj, value);
	}

	public void read_int8_t_array(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException, StructChangeException {
		read_uint8_t_array(obj, field);
	}

	public void read_struct_t(Object obj, Field field) throws Exception {
		struct_t fieldInf = field.getAnnotation(struct_t.class);

		Object childObj = fieldInf.cls().newInstance();
		_struct2obj_ex(childObj);
		field.set(obj, childObj);
	}

	public void read_struct_array(Object obj, Field field) throws Exception {
		@SuppressWarnings("unchecked")
		List<Object> values = (List<Object>) field.get(obj);
		if (values == null) {
			values = new ArrayList<Object>();
			field.set(obj, values);
		}

		struct_array fieldInf = field.getAnnotation(struct_array.class);
		int dataSize = 0;

		if (fieldInf.size() > 0)
			dataSize = fieldInf.size();
		else
			dataSize = getSizeFieldValue(obj, fieldInf.sizeField());

		if (dataSize == 0) { // unknown data size.
			while (true) {
				if (buf.position() == buf.capacity())
					break;
				readChildStruct(values, fieldInf);
			}
		} else {
			for (int i = 0; i < dataSize; i++) {
				readChildStruct(values, fieldInf);
			}
		}
	}

	private void readChildStruct(List<Object> values, struct_array fieldInf) throws Exception {
		Object childObj = fieldInf.cls().newInstance();
		_struct2obj_ex(childObj);
		values.add(childObj);
	}

	private void _struct2obj_ex(Object obj) throws Exception {
		if (obj instanceof SpecificCodecImpl) {
			((SpecificCodecImpl) obj).fromWire(buf);
		} else {
			_struct2obj(obj, obj.getClass());
		}
	}

	private int getSizeFieldValue(Object obj, String fieldName)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return Class2Struct.getSizeFieldValue(obj, fieldName);
	}
}
