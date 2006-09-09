package net.sf.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;

import junit.framework.TestCase;

public class DatabaseSchemaVersionManagerTest extends TestCase {

	static final String CONNECTION_STRING = "jdbc:oracle:thin:@localhost:1521:orcl";
	static final String USERNAME = "dbdeploy";
	static final String PASSWORD = "dbdeploy";
	private DatabaseSchemaVersionManager databaseSchemaVersion;
	
	@Override
	protected void setUp() throws Exception {
		DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

		databaseSchemaVersion = new DatabaseSchemaVersionManager(CONNECTION_STRING, USERNAME, PASSWORD);
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
	
	public void testShouldSetCurrentVersionToZeroWhenTableHasNoRows() throws Exception {
		ensureTableDoesNotExist();
		createTable();

		assertEquals(0, databaseSchemaVersion.getCurrentVersion());
	}
	
	public void testCanRetrieveSqlToUpdateTable() throws Exception {
		assertEquals("UPDATE DatabaseConfiguration SET SchemaVersion = 3", databaseSchemaVersion.generateSqlToUpdateSchemaVersion(3));
	}

	private void createTable() throws Exception {
		executeSql("CREATE TABLE " + DatabaseSchemaVersionManager.TABLE_NAME + " (SchemaVersion NUMBER NOT NULL)");
	}

	private void insertRowIntoTable(int i) throws SQLException {
		executeSql("INSERT INTO " + DatabaseSchemaVersionManager.TABLE_NAME + " VALUES ( " + i + ")");
	}

	private void ensureTableDoesNotExist() {
		try {
			executeSql("DROP TABLE " + DatabaseSchemaVersionManager.TABLE_NAME);
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
