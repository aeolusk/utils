package com.celizion.kcg.ems.ftp.controller;

public class FTPConnectionAlreadyUsedException extends Exception {
	private static final long serialVersionUID = -5592642886698328799L;

	public FTPConnectionAlreadyUsedException(String message) {
		super(message);
	}
}
