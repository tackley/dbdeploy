package net.sf.dbdeploy.database.changelog;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSqlDatabaseSchemaVersionManagerTest extends AbstractDatabaseSchemaVersionManagerTestBase {


	static final String CONNECTION_STRING = "jdbc:jtds:sqlserver://127.0.0.1:1170/dbdeploy";
	static final String USERNAME = "sa";
	static final String PASSWORD = "p@55word";
	static final String DELTA_SET = "All";
	static final String CHANGELOG_TABLE_DOES_NOT_EXIST_MESSAGE = "Could not retrieve change log from database because: Invalid object name 'changelog'.";

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
				"change_number INTEGER NOT NULL, " +
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
