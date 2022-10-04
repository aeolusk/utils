package com.bono.struct;

import java.nio.ByteBuffer;

public interface SpecificCodecImpl {
	public void fromWire(ByteBuffer readBuf) throws Exception;
	public void toWire(ByteBuffer writeBuf) throws Exception;

}
