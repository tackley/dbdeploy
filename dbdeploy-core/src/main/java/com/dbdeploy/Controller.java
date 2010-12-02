package com.dbdeploy;

import com.dbdeploy.appliers.ApplyMode;
import com.dbdeploy.appliers.ChangeScriptApplier;
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

	private final PrettyPrinter prettyPrinter = new PrettyPrinter();

	public Controller(AvailableChangeScriptsProvider availableChangeScriptsProvider,
					  AppliedChangesProvider appliedChangesProvider,
					  ChangeScriptApplier changeScriptApplier) {
		this.availableChangeScriptsProvider = availableChangeScriptsProvider;
		this.appliedChangesProvider = appliedChangesProvider;
		this.changeScriptApplier = changeScriptApplier;
	}

	public void processChangeScripts(int targetVersion) throws DbDeployException, IOException {
		if (targetVersion == Integer.MAX_VALUE)
			info("Upgrading database to the latest and greatest");
		else
		    info("Target version of database: " + targetVersion);
		    

		List<ChangeScript> availableScripts = availableChangeScriptsProvider.getAvailableChangeScripts();
		List<Integer> appliedScripts = appliedChangesProvider.getAppliedChanges();
		
		int latestScriptApplied = getLatestScriptApplied(appliedScripts);
		ApplyMode applyMode = getApplyMode(latestScriptApplied, targetVersion);
		List<ChangeScript> toApply = identifyChangesToApply(targetVersion, appliedScripts, availableScripts, applyMode);
		
		if (applyMode == ApplyMode.UNDO) Collections.reverse(toApply);
		logStatus(availableScripts, appliedScripts, toApply, applyMode);
		changeScriptApplier.apply(Collections.unmodifiableList(toApply), applyMode);
	}

    /**
	 * returns the current version of the database
	 */
    private int getLatestScriptApplied(List<Integer> applied) {
        int latestScriptApplied = 0;

        for (Integer i : applied) {
            if (i > latestScriptApplied)
                latestScriptApplied = i;
        }
        
        return latestScriptApplied;
    }
    
	private List<ChangeScript> identifyChangesToApply(int targetVersion, List<Integer> appliedScripts
	        , List<ChangeScript> availableScripts, ApplyMode applyMode) {
		List<ChangeScript> changesToApply = new ArrayList<ChangeScript>();

		for (ChangeScript script : availableScripts) {
    		if (applyMode == ApplyMode.DO) {
    		    if (script.getId() <= targetVersion && !appliedScripts.contains(script.getId()))
    		        changesToApply.add(script);
    		}
    		else if (applyMode == ApplyMode.UNDO) {
    		    if (script.getId() > targetVersion && appliedScripts.contains(script.getId()))
                    changesToApply.add(script);
    		}
		}

		return changesToApply;
	}
	
	private ApplyMode getApplyMode(int currentVersion, int targetVersion) {
        if (currentVersion <= targetVersion)
            return ApplyMode.DO;
        else 
            return ApplyMode.UNDO;
    }
	
	private void logStatus(List<ChangeScript> scripts, List<Integer> applied, List<ChangeScript> toApply, ApplyMode applyMode) {
        info("Changes currently applied to database:\n  " + prettyPrinter.format(applied));
        info("Scripts available:\n  " + prettyPrinter.formatChangeScriptList(scripts));
        info("To be applied:\n  " + prettyPrinter.formatChangeScriptList(toApply));
        info("Apply mode: " + applyMode);
    }

	private void info(String string) {
		System.err.println(string);
	}
}