package com.dbdeploy;

import java.util.List;

public interface ChangeScriptValidatorProvider {
    public List<? extends ChangeScriptValidator> getValidators();
}
