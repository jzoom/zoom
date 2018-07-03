package com.jzoom.zoom.admin.models;

import com.jzoom.zoom.web.exception.StatusException;

public class AdminException extends StatusException {

	public AdminException( String code, String error) {
		super(418, code, error);
	}
	public AdminException(  String error) {
		super(418, null, error);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3724820666213370467L;

}
