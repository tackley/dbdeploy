package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;

public class Controller {

	private final DatabaseSchemaVersionManager schemaVersion;
	private final ChangeScriptExecuter changeScriptExecuter;
	private final ChangeScriptRepository changeScriptRepository;
	private final PrettyPrinter prettyPrinter = new PrettyPrinter();

	public Controller(DatabaseSchemaVersionManager schemaVersion, 
			ChangeScriptRepository changeScriptRepository,
			ChangeScriptExecuter changeScriptExecuter) {
		this.schemaVersion = schemaVersion;
		this.changeScriptRepository = changeScriptRepository;
		this.changeScriptExecuter = changeScriptExecuter;
	}

	public void applyScriptsToGetChangesUpToLatestVersion(File directory) throws DbDeployException, IOException {
		
		List<ChangeScript> changeScripts = changeScriptRepository.getOrderedListOfChangeScripts();

		List<Integer> appliedChanges = schemaVersion.getAppliedChangeNumbers();

		info("Changes currently applied to database:\n  " + prettyPrinter.format(appliedChanges));
		info("Scripts available:\n  " + prettyPrinter.formatChangeScriptList(changeScripts));
		
		List<Integer> changesToApply = new ArrayList<Integer>();
		
		for (ChangeScript changeScript : changeScripts) {
			if (!appliedChanges.contains(changeScript.getId())) {
				changesToApply.add(changeScript.getId());
				changeScriptExecuter.applyChangeScript(changeScript);

				String sql = schemaVersion.generateSqlToUpdateSchemaVersion(changeScript);
				changeScriptExecuter.applySqlToSetSchemaVersion(sql);
			}
		}

		info("To be applied:\n  " + prettyPrinter.format(changesToApply));
	}

	private void info(String string) {
		System.err.println(string);
	}
}
