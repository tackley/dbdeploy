package com.dbdeploy.integration;


import com.dbdeploy.ChangeScriptValidator;
import com.dbdeploy.ChangeScriptFilter;
import com.dbdeploy.ChangeScriptValidationFailedException;
import com.dbdeploy.exceptions.DbDeployException;
import com.dbdeploy.scripts.ChangeScript;

import java.util.ArrayList;
import java.util.List;

public class ChangeScriptValidatingFilter implements ChangeScriptFilter {

    private List<? extends ChangeScriptValidator> getValidators() {
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

    public void process(List<ChangeScript> changeScripts) {
        for (ChangeScript changeScript : changeScripts) {
            applyValidations(changeScript);
        }
    }

    private void applyValidations(ChangeScript changeScript) {
        List<? extends ChangeScriptValidator> validators = getValidators();
        for (ChangeScriptValidator validator : validators) {
            validator.validate(changeScript);
        }

    }
}