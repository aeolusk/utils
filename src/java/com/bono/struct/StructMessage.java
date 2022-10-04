package com.bono.struct;

public interface StructMessage {
	public String getMIN();

	public long getPacketType();

	public long getLength();

	public boolean useEncrypt();
}
