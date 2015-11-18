package com.dbdeploy.integration;


import com.dbdeploy.ChangeScriptValidator;
import com.dbdeploy.ChangeScriptValidatorProvider;
import com.dbdeploy.exceptions.ChangeScriptValidationFailedException;
import com.dbdeploy.exceptions.DbDeployException;
import com.dbdeploy.scripts.ChangeScript;

import java.util.ArrayList;
import java.util.List;

public class ChangeScriptValidatorProviderImpl implements ChangeScriptValidatorProvider {

    public List<? extends ChangeScriptValidator> getValidators() {
        List<ChangeScriptValidator> validators = new ArrayList<ChangeScriptValidator>();
        validators.add(new ChangeScriptValidator() {
            public boolean validate(ChangeScript changeScript) throws DbDeployException {
                String changeScriptName = changeScript.getFile().getName();
                if (!changeScriptName.matches("[0-9]{14}_(.*)")) {
                    throw new ChangeScriptValidationFailedException("name should start with datetime format yyyyMMddHHmmss");
                }
                return Boolean.TRUE;
            }
        });
        return validators;
    }
}