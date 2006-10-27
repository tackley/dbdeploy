package net.sf.dbdeploy.scripts;

import java.util.Collections;
import java.util.List;

import net.sf.dbdeploy.exceptions.DuplicateChangeScriptException;


public class ChangeScriptRepository {

	private final List<ChangeScript> scripts;

	@SuppressWarnings("unchecked")
	public ChangeScriptRepository(List<ChangeScript> scripts) throws DuplicateChangeScriptException {
		this.scripts = scripts;

		Collections.sort(this.scripts);
		
		checkForDuplicateIds(scripts);
	}
	
	private void checkForDuplicateIds(List<ChangeScript> scripts) throws DuplicateChangeScriptException {
		int lastId = 0;
		
		for (ChangeScript script : scripts) {
			if (script.getId() == lastId) {
				throw new DuplicateChangeScriptException("There is more than one change script with number " + lastId);
			}
			
			lastId = script.getId();
		}
		
	}

	public List<ChangeScript> getOrderedListOfDoChangeScripts() {
		return Collections.unmodifiableList(scripts);
	}

	public List<ChangeScript> getOrderedListOfUndoChangeScripts() {
		Collections.reverse(this.scripts);
		return Collections.unmodifiableList(scripts);
	}

}
