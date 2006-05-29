package net.tackley.dbdeploy.scripts;

import java.util.Arrays;

import net.tackley.dbdeploy.exceptions.RequiredChangeScriptNotFoundException;


public class ChangeScriptRepository {

	private final ChangeScript[] scripts;

	public ChangeScriptRepository(ChangeScript[] scripts) {
		this.scripts = scripts;
		Arrays.sort(this.scripts);
	}
	
	public int getHighestAvailableChangeScript() {
		return scripts[scripts.length - 1].getId();
	}

	public ChangeScript getChangeScript(int scriptVerison) throws RequiredChangeScriptNotFoundException {
		for (ChangeScript changeScript : scripts) {
			if (changeScript.getId() == scriptVerison)
				return changeScript;
		}
		throw new RequiredChangeScriptNotFoundException("Change script #" + scriptVerison + " was not found and is required");
	}

}
