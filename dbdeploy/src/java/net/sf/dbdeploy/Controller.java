package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;

public class Controller {

	private final DatabaseSchemaVersionManager schemaVersion;
	private final ChangeScriptExecuter changeScriptExecuter;
	private final ChangeScriptRepository changeScriptRepository;

	public Controller(DatabaseSchemaVersionManager schemaVersion, 
			ChangeScriptRepository changeScriptRepository,
			ChangeScriptExecuter changeScriptExecuter) {
		this.schemaVersion = schemaVersion;
		this.changeScriptRepository = changeScriptRepository;
		this.changeScriptExecuter = changeScriptExecuter;
	}

	public void applyScriptsToGetChangesUpToLatestVersion(File directory) throws DbDeployException, IOException {
		
		List<ChangeScript> changeScripts = changeScriptRepository.getOrderedListOfChangeScripts();

		info("Reading changes applied to database...");

		Set<Integer> appliedChanges = schemaVersion.getAppliedChangeNumbers();

		int count = 0;
		for (ChangeScript changeScript : changeScripts) {
			if (!appliedChanges.contains(changeScript.getId())) {
				count++;
				info("Need to apply change script " + changeScript);
				changeScriptExecuter.applyChangeScript(changeScript);

				String sql = schemaVersion.generateSqlToUpdateSchemaVersion(changeScript);
				changeScriptExecuter.applySqlToSetSchemaVersion(sql);
			}
		}
		
		info(count + " script(s) selected for application");
	}

	private void info(String string) {
		System.err.println(string);
	}
}
