package com.dbdeploy.scripts;

import com.dbdeploy.AvailableChangeScriptsProvider;
import com.dbdeploy.exceptions.DuplicateChangeScriptException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChangeScriptRepository implements AvailableChangeScriptsProvider {

	private final List<ChangeScript> scripts;

	@SuppressWarnings("unchecked")
	public ChangeScriptRepository(List<ChangeScript> scripts) throws DuplicateChangeScriptException {
		this.scripts = scripts;

		Collections.sort(this.scripts);
		
		checkForDuplicateIds(scripts);
	}
	
	private void checkForDuplicateIds(List<ChangeScript> scripts) throws DuplicateChangeScriptException {
		long lastId = -1;

        List<Long> duplicateIds = new ArrayList<Long>();
		for (ChangeScript script : scripts) {
			if (script.getId() == lastId) {
				duplicateIds.add(script.getId());
			}

			lastId = script.getId();
		}
        if (!duplicateIds.isEmpty()) {
            throw new DuplicateChangeScriptException("There is more than one change script with number " + toCSV(duplicateIds));
        }

	}

    private String toCSV(List c) {
        String str = "";
        for (int i = 0; i < c.size(); i++) {
            if (i != 0) {
                str = str + ",";
            }
            str = str + c.get(i).toString();
        }
        return str;
    }

	public List<ChangeScript> getOrderedListOfDoChangeScripts() {
		return Collections.unmodifiableList(scripts);
	}

	public List<ChangeScript> getAvailableChangeScripts() {
		return getOrderedListOfDoChangeScripts();
	}
}
