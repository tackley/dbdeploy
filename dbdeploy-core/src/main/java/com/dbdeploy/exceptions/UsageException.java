package com.dbdeploy.exceptions;

public class UsageException extends DbDeployException {
    private static final long serialVersionUID = 1L;

	public UsageException(String message) {
		super(message);
	}

	public UsageException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public static void throwForMissingRequiredValue(String valueName) throws UsageException {
		throw new UsageException(valueName + " required");
	}

}
