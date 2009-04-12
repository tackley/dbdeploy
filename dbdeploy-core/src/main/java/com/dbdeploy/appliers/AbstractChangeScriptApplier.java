package com.dbdeploy.appliers;

import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.ChangeScriptApplier;

public abstract class AbstractChangeScriptApplier implements ChangeScriptApplier {
	private final ApplyMode mode;

	protected AbstractChangeScriptApplier(ApplyMode mode) {
		this.mode = mode;
	}

	@Override
	public void apply(ChangeScript changeScript) {
		preChangeScriptApply(changeScript);
		beginTransaction();

		if (mode == ApplyMode.DO) {
			applyChangeScriptContent(changeScript.getContent());
			insertToSchemaVersionTable(changeScript);
		} else {
			applyChangeScriptContent(changeScript.getUndoContent());
			deleteFromSchemaVersionTable(changeScript);
		}
		commitTransaction();
		postChangeScriptApply(changeScript);
	}


	protected abstract void preChangeScriptApply(ChangeScript changeScript);

	protected abstract void beginTransaction();

	protected abstract void applyChangeScriptContent(String scriptContent);

	protected abstract void insertToSchemaVersionTable(ChangeScript changeScript);

	protected abstract void deleteFromSchemaVersionTable(ChangeScript changeScript);

	protected abstract void commitTransaction();

	protected abstract void postChangeScriptApply(ChangeScript changeScript);
}
