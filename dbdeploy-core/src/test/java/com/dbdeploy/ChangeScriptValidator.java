package com.dbdeploy;

import com.dbdeploy.exceptions.DbDeployException;
import com.dbdeploy.scripts.ChangeScript;

public interface ChangeScriptValidator {
    boolean validate(ChangeScript changeScript) throws DbDeployException;
}
