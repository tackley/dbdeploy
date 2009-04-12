package com.dbdeploy.exceptions;

public class DuplicateChangeScriptException extends DbDeployException {

	private static final long serialVersionUID = 1L;

	public DuplicateChangeScriptException() {
		super();
	}

	public DuplicateChangeScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateChangeScriptException(String message) {
		super(message);
	}

	public DuplicateChangeScriptException(Throwable cause) {
		super(cause);
	}
}
