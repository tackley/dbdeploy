package net.tackley.dbdeploy;

import net.tackley.dbdeploy.database.DatabaseSchemaVersion;
import net.tackley.dbdeploy.exceptions.RequiredChangeScriptNotFoundException;
import net.tackley.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.tackley.dbdeploy.scripts.ChangeScript;
import net.tackley.dbdeploy.scripts.ChangeScriptRepository;

public class Controller {

	private Output output;
	private DatabaseSchemaVersion schemaVersion;
	private ChangeScriptRepository changeScriptRepository;
	private ChangeScriptExecuter changeScriptExecuter;

	public Controller(Output output, DatabaseSchemaVersion schemaVersion,
			ChangeScriptRepository changeScriptRepository, ChangeScriptExecuter changeScriptExecuter) {
		this.output = output;
		this.schemaVersion = schemaVersion;
		this.changeScriptRepository = changeScriptRepository;
		this.changeScriptExecuter = changeScriptExecuter;
	}

	public void applyScriptToGetChangesUpToLatestVersion() throws SchemaVersionTrackingException, RequiredChangeScriptNotFoundException {
		int currentSchemaVersion = schemaVersion.getCurrentVersion();

		output.info("Current schema version: " + currentSchemaVersion);

		int highestSchemaVersionScriptAvailable = changeScriptRepository.getHighestAvailableChangeScript();

		output.info("Change scripts available to version: " + highestSchemaVersionScriptAvailable);

		for (int scriptNumber = currentSchemaVersion + 1; scriptNumber < highestSchemaVersionScriptAvailable; scriptNumber++) {
			ChangeScript changeScript = changeScriptRepository.getChangeScript(currentSchemaVersion);
			changeScriptExecuter.applyChangeScript(changeScript);
			schemaVersion.setCurrentVersion(scriptNumber);
		}

	}
}
