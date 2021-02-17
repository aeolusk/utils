package com.celizion.kcg.ems.tta.define;

public enum HeaderStructure {
	Ptype(4, 0),
	MsgID(4, 4),
	segmentIndicator(4, 8),
	Length(4, 12)
	;
	
	public final int length;
	public final int offset;
	
	private HeaderStructure(int length, int offset) {
		this.length = length;
		this.offset = offset;
	}
	
	private static int headerLength = 0;
	public synchronized static int getLength() {
		if(headerLength > 0)
			return headerLength;
		
		for(HeaderStructure value : values()) {
			headerLength += value.length;
		}
		return headerLength;
	}
}

