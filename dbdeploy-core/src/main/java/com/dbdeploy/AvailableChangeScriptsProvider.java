package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;

import java.util.List;

public interface AvailableChangeScriptsProvider {
	List<ChangeScript> getAvailableChangeScripts();
}
