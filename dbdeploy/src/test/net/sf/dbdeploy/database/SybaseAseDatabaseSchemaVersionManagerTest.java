package net.sf.dbdeploy.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SybaseAseDatabaseSchemaVersionManagerTest extends AbstractDatabaseSchemaVersionManager {


	static final String CONNECTION_STRING = "jdbc:jtds:sybase://192.168.205.128:1276/dbdeploy";
	static final String USERNAME = "sa";
	static final String PASSWORD = "";
	static final String DELTA_SET = "All";
	static final String CHANGELOG_TABLE_DOES_NOT_EXIST_MESSAGE = "Could not retrieve change log from database because: changelog not found. Specify owner.objectname or use sp_help to check whether the object exists (sp_help may produce lots of output).\n";
	
	protected String getConnectionString() {
		return CONNECTION_STRING;
	}
	
	protected String getUsername() {
		return USERNAME;
	}
	
	protected String getPassword() {
		return PASSWORD;
	}
	
	protected String getDeltaSet() {
		return DELTA_SET;
	}
	
	protected String getChangelogTableDoesNotExistMessage() {
		return CHANGELOG_TABLE_DOES_NOT_EXIST_MESSAGE;
	}
	
	protected void createTable() throws SQLException {
		executeSql(
		"CREATE TABLE " + DatabaseSchemaVersionManager.TABLE_NAME + "( " +
				"change_number INTEGER, " +
				"delta_set VARCHAR(10) NOT NULL, " +
				"start_dt DATETIME NOT NULL, " +
				"complete_dt DATETIME NOT NULL, " +
				"applied_by VARCHAR(100) NOT NULL, "+
			    "description VARCHAR(500) NOT NULL )");
		executeSql(
		"ALTER TABLE " + DatabaseSchemaVersionManager.TABLE_NAME + 
			" ADD CONSTRAINT PK_VERSION_MGR PRIMARY KEY (change_number, delta_set)");

	}

	protected void insertRowIntoTable(int i) throws SQLException {
		executeSql("INSERT INTO " + DatabaseSchemaVersionManager.TABLE_NAME 
				+ " (change_number, delta_set, start_dt, complete_dt, applied_by, description) VALUES ( " 
				+ i + ", '" + DELTA_SET 
				+ "', getdate(), getdate(), user_name(), 'Unit test')");
	}

	protected void registerDbDriver() throws SQLException {
		DriverManager.registerDriver (new net.sourceforge.jtds.jdbc.Driver());
	}

}
