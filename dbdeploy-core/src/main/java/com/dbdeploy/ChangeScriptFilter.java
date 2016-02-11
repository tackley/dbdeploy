package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;

import java.util.List;

public interface ChangeScriptFilter {

    public void process(List<ChangeScript> changeScripts);
}
