package com.bono.struct.exception;

public class InvalidDataException extends Exception {
	private static final long serialVersionUID = 5587621202262534335L;

	public InvalidDataException(String message) {
		super(message);
	}
}
