package net.tackley.dbdeploy;

public interface DatabaseSchemaVersion {

	int getCurrentVersion();
	void setCurrentVersion(int scriptNumber);

}
