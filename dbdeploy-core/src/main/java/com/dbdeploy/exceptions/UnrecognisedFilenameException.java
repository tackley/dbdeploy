package com.dbdeploy.exceptions;

public class UnrecognisedFilenameException extends DbDeployException {

	private static final long serialVersionUID = 1L;

	public UnrecognisedFilenameException() {
		super();
		
	}

	public UnrecognisedFilenameException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UnrecognisedFilenameException(String message) {
		super(message);
		
	}

	public UnrecognisedFilenameException(Throwable cause) {
		super(cause);
		
	}
	
	

}
