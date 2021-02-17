package com.celizion.kcg.ems.tta.controller;

import java.nio.ByteOrder;

import com.celizion.kcg.ems.tta.define.HeaderStructure;

public interface MessageDefinition {
	public static final int MAX_PACKET_LENGTH = 4096;
	public static final int HEADER_PREFIX_LENGTH = HeaderStructure.getLength();
	public static final int MAX_PARAMETERS_LENGTH = MAX_PACKET_LENGTH - HEADER_PREFIX_LENGTH;

	public static final int SEGMENT_NONE_DIVIDE = 0;
	public static final int SEGMENT_DIVIDE = 1;

	public static ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;
}
