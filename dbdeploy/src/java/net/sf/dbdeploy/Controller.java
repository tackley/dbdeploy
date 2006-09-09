package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;

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
		
		int highestSchemaVersionScriptAvailable = changeScriptRepository.getHighestAvailableChangeScript();

		info("Change scripts available to schema version: " + highestSchemaVersionScriptAvailable);

		info("Reading current schema version from database...");

		int currentSchemaVersion = schemaVersion.getCurrentVersion();

		info("Current schema version: " + currentSchemaVersion);


		for (int scriptNumber = currentSchemaVersion + 1; scriptNumber <= highestSchemaVersionScriptAvailable; scriptNumber++) {
			
			ChangeScript changeScript = changeScriptRepository.getChangeScript(scriptNumber);

			info("Applying change script " + changeScript);
			changeScriptExecuter.applyChangeScript(changeScript);

			String sql = schemaVersion.generateSqlToUpdateSchemaVersion(scriptNumber);
			changeScriptExecuter.applySqlToSetSchemaVersion(sql);
		}

	}

	private void info(String string) {
		System.out.println(string);
	}
}
