package com.dbdeploy.exceptions;

public class RequiredChangeScriptNotFoundException extends DbDeployException {

	private static final long serialVersionUID = 1L;

	public RequiredChangeScriptNotFoundException() {
		super();
		
	}

	public RequiredChangeScriptNotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public RequiredChangeScriptNotFoundException(String message) {
		super(message);
		
	}

	public RequiredChangeScriptNotFoundException(Throwable cause) {
		super(cause);
		
	}

}
