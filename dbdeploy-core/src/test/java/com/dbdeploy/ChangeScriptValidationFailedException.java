package com.dbdeploy;

import com.dbdeploy.exceptions.DbDeployException;

public class ChangeScriptValidationFailedException extends DbDeployException {
    public ChangeScriptValidationFailedException() {
    }

    public ChangeScriptValidationFailedException(String message) {
        super(message);
    }

    public ChangeScriptValidationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChangeScriptValidationFailedException(Throwable cause) {
        super(cause);
    }
}
