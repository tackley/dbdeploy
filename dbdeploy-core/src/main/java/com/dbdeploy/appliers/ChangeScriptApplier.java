package com.dbdeploy.appliers;

import com.dbdeploy.scripts.ChangeScript;

import java.util.List;

public interface ChangeScriptApplier {
	void apply(List<ChangeScript> changeScript, ApplyMode applyMode);
}
