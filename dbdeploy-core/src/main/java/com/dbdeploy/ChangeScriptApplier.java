package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;

import java.util.List;

public interface ChangeScriptApplier {
	void apply(List<ChangeScript> changeScript);
}
