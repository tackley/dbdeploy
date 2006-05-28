package net.tackley.dbdeploy;

public interface ChangeScriptRepository {

	int getHighestAvailableChangeScript();

	ChangeScript getChangeScript(int currentSchemaVersion);

}
