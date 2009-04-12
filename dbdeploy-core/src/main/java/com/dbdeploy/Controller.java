package com.dbdeploy;

import com.dbdeploy.exceptions.DbDeployException;
import com.dbdeploy.scripts.ChangeScript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {

	private final AvailableChangeScriptsProvider availableChangeScriptsProvider;
	private final AppliedChangesProvider appliedChangesProvider;
	private final ChangeScriptApplier changeScriptApplier;
	private final ChangeScriptApplier undoScriptApplier;

	private final PrettyPrinter prettyPrinter = new PrettyPrinter();

	public Controller(AvailableChangeScriptsProvider availableChangeScriptsProvider,
					  AppliedChangesProvider appliedChangesProvider,
					  ChangeScriptApplier changeScriptApplier, ChangeScriptApplier undoScriptApplier) {
		this.availableChangeScriptsProvider = availableChangeScriptsProvider;
		this.appliedChangesProvider = appliedChangesProvider;
		this.changeScriptApplier = changeScriptApplier;
		this.undoScriptApplier = undoScriptApplier;
	}

	public void processChangeScripts(Integer lastChangeToApply) throws DbDeployException, IOException {
		if (lastChangeToApply != Integer.MAX_VALUE) {
			info("Only applying changes up and including change script #" + lastChangeToApply);
		}

		List<ChangeScript> scripts = availableChangeScriptsProvider.getAvailableChangeScripts();
		List<Integer> applied = appliedChangesProvider.getAppliedChanges();
		List<ChangeScript> toApply = identifyChangesToApply(lastChangeToApply, scripts, applied);

		logStatus(scripts, applied, toApply);

		applyScripts(toApply, changeScriptApplier);

		if (undoScriptApplier != null) {
			info("Generating undo scripts...");
			Collections.reverse(toApply);
			applyScripts(toApply, undoScriptApplier);
		}
	}

	private void applyScripts(List<ChangeScript> toApply, ChangeScriptApplier applier) {
		applier.begin();

		for (ChangeScript changeScript : toApply) {
			applier.apply(changeScript);
		}

		applier.end();
	}

	private void logStatus(List<ChangeScript> scripts, List<Integer> applied, List<ChangeScript> toApply) {
		info("Changes currently applied to database:\n  " + prettyPrinter.format(applied));
		info("Scripts available:\n  " + prettyPrinter.formatChangeScriptList(scripts));
		info("To be applied:\n  " + prettyPrinter.formatChangeScriptList(toApply));
	}

	private List<ChangeScript> identifyChangesToApply(Integer lastChangeToApply, List<ChangeScript> scripts, List<Integer> applied) {
		List<ChangeScript> result = new ArrayList<ChangeScript>();

		for (ChangeScript script : scripts) {
			if (script.getId() > lastChangeToApply)
				break;

			if (!applied.contains(script.getId())) {
				result.add(script);
			}
		}

		return result;
	}

	private void info(String string) {
		System.err.println(string);
	}
}