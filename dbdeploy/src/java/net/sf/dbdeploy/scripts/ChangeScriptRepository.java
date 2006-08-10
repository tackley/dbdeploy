package net.sf.dbdeploy.scripts;

import java.util.Arrays;

import net.sf.dbdeploy.exceptions.DuplicateChangeScriptException;
import net.sf.dbdeploy.exceptions.RequiredChangeScriptNotFoundException;


public class ChangeScriptRepository {

	private final ChangeScript[] scripts;

	public ChangeScriptRepository(ChangeScript[] scripts) throws DuplicateChangeScriptException {
		this.scripts = scripts;
		Arrays.sort(this.scripts);
		
		checkForDuplicateIds(scripts);
	}
	
	private void checkForDuplicateIds(ChangeScript[] scripts) throws DuplicateChangeScriptException {
		int lastId = 0;
		
		for (ChangeScript script : scripts) {
			if (script.getId() == lastId) {
				throw new DuplicateChangeScriptException("There is more than one change script with number " + lastId);
			}
			
			lastId = script.getId();
		}
		
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
