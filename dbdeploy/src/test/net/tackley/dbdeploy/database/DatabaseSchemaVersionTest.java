package net.tackley.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.tackley.dbdeploy.database.DatabaseSchemaVersion;
import net.tackley.dbdeploy.exceptions.SchemaVersionTrackingException;

import junit.framework.TestCase;

public class DatabaseSchemaVersionTest extends TestCase {

	static final String CONNECTION_STRING = "jdbc:oracle:thin:@localhost:1521:orcl";
	static final String USERNAME = "dbdeploy";
	static final String PASSWORD = "dbdeploy";
	private DatabaseSchemaVersion databaseSchemaVersion;
	
	@Override
	protected void setUp() throws Exception {
		DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

		databaseSchemaVersion = new DatabaseSchemaVersion(CONNECTION_STRING, USERNAME, PASSWORD);
	}
	
	public void testCanRetrieveSchemaVersionFromDatabase() throws Exception {
		ensureTableDoesNotExist();
		createTable();
		insertRowIntoTable(5);

		assertEquals(5, databaseSchemaVersion.getCurrentVersion());
	}
	

	public void testThrowsWhenDatabaseTableDoesNotExist() throws Exception {
		ensureTableDoesNotExist();
		
		try {
			databaseSchemaVersion.getCurrentVersion();
			fail("expected exception");
		} catch (SchemaVersionTrackingException ex) {
			assertEquals("Could not retrieve schema version from database because: ORA-00942: table or view does not exist\n" , ex.getMessage());
		}
	}
	
	public void testThrowsWhenDatabaseTableExistsButHasNoRows() throws Exception {
		ensureTableDoesNotExist();
		createTable();

		try {
			databaseSchemaVersion.getCurrentVersion();
			fail("expected exception");
		} catch (SchemaVersionTrackingException ex) {
			assertEquals("Could not retrieve schema version from database because: table exists but is empty\n" , ex.getMessage());
		}

	}
	
	public void testCanUpdateSchemaVersion() throws Exception {
		ensureTableDoesNotExist();
		createTable();
		insertRowIntoTable(5);

		databaseSchemaVersion.setCurrentVersion(6);
		assertEquals(6, databaseSchemaVersion.getCurrentVersion());
		
	}

	private void createTable() throws Exception {
		executeSql("CREATE TABLE " + DatabaseSchemaVersion.TABLE_NAME + " (SchemaVersion NUMBER NOT NULL)");
	}

	private void insertRowIntoTable(int i) throws SQLException {
		executeSql("INSERT INTO " + DatabaseSchemaVersion.TABLE_NAME + " VALUES ( " + i + ")");
	}

	private void ensureTableDoesNotExist() {
		try {
			executeSql("DROP TABLE " + DatabaseSchemaVersion.TABLE_NAME);
		} catch (Exception e) {
			// ignore
		}
	}
	
	private void executeSql(String sql) throws SQLException {
		DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
		Connection connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
	}

}
