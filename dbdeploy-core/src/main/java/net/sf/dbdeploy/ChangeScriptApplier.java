package net.sf.dbdeploy;

import net.sf.dbdeploy.scripts.ChangeScript;

public interface ChangeScriptApplier {
	void begin();

	void apply(ChangeScript changeScript);

	void end();
}
