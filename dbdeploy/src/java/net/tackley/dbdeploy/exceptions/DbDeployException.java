package net.tackley.dbdeploy.exceptions;

public class DbDeployException extends Exception {

	private static final long serialVersionUID = 1L;

	public DbDeployException() {
		super();

	}

	public DbDeployException(String message) {
		super(message);

	}

	public DbDeployException(String message, Throwable cause) {
		super(message, cause);

	}

	public DbDeployException(Throwable cause) {
		super(cause);

	}

}
