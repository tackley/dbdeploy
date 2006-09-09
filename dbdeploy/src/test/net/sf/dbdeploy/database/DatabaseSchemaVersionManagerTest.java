package net.sf.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;

import org.jmock.cglib.MockObjectTestCase;

public class DatabaseSchemaVersionManagerTest extends MockObjectTestCase {

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

		Set<Integer> appliedChangeNumbers = databaseSchemaVersion.getAppliedChangeNumbers();
		assertEquals(1, appliedChangeNumbers.size());
		assertThat(new Integer(5), isIn(appliedChangeNumbers));
	}
	

	public void testThrowsWhenDatabaseTableDoesNotExist() throws Exception {
		ensureTableDoesNotExist();
		
		try {
			databaseSchemaVersion.getAppliedChangeNumbers();
			fail("expected exception");
		} catch (SchemaVersionTrackingException ex) {
			assertEquals("Could not retrieve change log from database because: ORA-00942: table or view does not exist\n" , ex.getMessage());
		}
	}
	
	public void testShouldReturnEmptySetWhenTableHasNoRows() throws Exception {
		ensureTableDoesNotExist();
		createTable();

		assertEquals(0, databaseSchemaVersion.getAppliedChangeNumbers().size());
	}
	
	public void testCanRetrieveSqlToUpdateTable() throws Exception {
		ChangeScript script = new ChangeScript(3, "description"); 
		assertEquals("INSERT INTO changelog (change_number, description) VALUES (3, 'description')", 
				databaseSchemaVersion.generateSqlToUpdateSchemaVersion(script));
	}

	private void createTable() throws Exception {
		executeSql(
		"CREATE TABLE " + DatabaseSchemaVersionManager.TABLE_NAME + "( " +
				"change_number INTEGER PRIMARY KEY, " +
				"applied_date TIMESTAMP DEFAULT (CURRENT_TIMESTAMP) NOT NULL, " +
				"applied_by VARCHAR2(100) DEFAULT (USER) NOT NULL, "+
			    "description VARCHAR2(500) NOT NULL )");

	}

	private void insertRowIntoTable(int i) throws SQLException {
		executeSql("INSERT INTO " + DatabaseSchemaVersionManager.TABLE_NAME 
				+ " (change_number, description) VALUES ( " + i + ", 'Unit test')");
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
