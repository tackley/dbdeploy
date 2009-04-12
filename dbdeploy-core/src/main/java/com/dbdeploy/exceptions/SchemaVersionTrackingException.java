package com.dbdeploy.exceptions;

public class SchemaVersionTrackingException extends DbDeployException {

	private static final long serialVersionUID = 1L;

	public SchemaVersionTrackingException() {
		super();
		
	}

	public SchemaVersionTrackingException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SchemaVersionTrackingException(String message) {
		super(message);
		
	}

	public SchemaVersionTrackingException(Throwable cause) {
		super(cause);
		
	}
	
	

}
