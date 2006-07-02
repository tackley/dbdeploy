package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import net.sf.dbdeploy.scripts.ChangeScriptRepositoryFactory;

public class Controller {

	private Output output;
	private DatabaseSchemaVersionManager schemaVersion;
	private ChangeScriptExecuter changeScriptExecuter;
	private ChangeScriptRepositoryFactory changeScriptRepositoryFactory;

	public Controller(Output output, 
			DatabaseSchemaVersionManager schemaVersion,
			ChangeScriptRepositoryFactory changeScriptRepositoryFactory, 
			ChangeScriptExecuter changeScriptExecuter) {
		this.output = output;
		this.schemaVersion = schemaVersion;
		this.changeScriptRepositoryFactory = changeScriptRepositoryFactory;
		this.changeScriptExecuter = changeScriptExecuter;
	}

	public void applyScriptToGetChangesUpToLatestVersion(File directory) throws DbDeployException, IOException {
		
		output.debug("Reading change scripts from directory " + directory.getCanonicalPath() + "...");
		
		ChangeScriptRepository changeScriptRepository = changeScriptRepositoryFactory.create(directory);

		int highestSchemaVersionScriptAvailable = changeScriptRepository.getHighestAvailableChangeScript();

		output.info("Change scripts available to schema version: " + highestSchemaVersionScriptAvailable);

		output.debug("Reading current schema version from database...");

		int currentSchemaVersion = schemaVersion.getCurrentVersion();

		output.info("Current schema version: " + currentSchemaVersion);


		for (int scriptNumber = currentSchemaVersion + 1; scriptNumber <= highestSchemaVersionScriptAvailable; scriptNumber++) {
			
			ChangeScript changeScript = changeScriptRepository.getChangeScript(scriptNumber);

			output.info("Applying change script " + changeScript);
			changeScriptExecuter.applyChangeScript(changeScript);

			String sql = schemaVersion.generateSqlToUpdateSchemaVersion(scriptNumber);
			changeScriptExecuter.applySqlToSetSchemaVersion(sql);
		}

	}
}
