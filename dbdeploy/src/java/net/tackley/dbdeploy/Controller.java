package net.tackley.dbdeploy;

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

	public void applyScriptToGetChangesUpToLatestVersion() {
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
