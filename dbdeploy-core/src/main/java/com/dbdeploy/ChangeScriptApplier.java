package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;

public interface ChangeScriptApplier {
	void begin();

	void apply(ChangeScript changeScript);

	void end();
}
