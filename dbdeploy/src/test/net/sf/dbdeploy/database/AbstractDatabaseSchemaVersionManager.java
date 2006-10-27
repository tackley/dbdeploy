package net.sf.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;

import org.jmock.cglib.MockObjectTestCase;


public abstract class AbstractDatabaseSchemaVersionManager extends MockObjectTestCase {
	
	private DatabaseSchemaVersionManager databaseSchemaVersion;
	private String connectionString = getConnectionString();
	private String username = getUsername();
	private String password = getPassword();
	private String deltaSet = getDeltaSet();
	private String changelogTableDoesNotExistMessage = getChangelogTableDoesNotExistMessage();
	
	@Override
	protected void setUp() throws Exception {
		//DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
		registerDbDriver();

		databaseSchemaVersion = new DatabaseSchemaVersionManager(connectionString, username, password, new OracleDbmsSyntax(), deltaSet);
	}
	
	public void testCanRetrieveSchemaVersionFromDatabase() throws Exception {
		ensureTableDoesNotExist();
		createTable();
		insertRowIntoTable(5);

		List<Integer> appliedChangeNumbers = databaseSchemaVersion.getAppliedChangeNumbers();
		assertEquals(1, appliedChangeNumbers.size());
		assertThat(new Integer(5), isIn(appliedChangeNumbers));
	}
	

	public void testThrowsWhenDatabaseTableDoesNotExist() throws Exception {
		ensureTableDoesNotExist();
		
		try {
			databaseSchemaVersion.getAppliedChangeNumbers();
			fail("expected exception");
		} catch (SchemaVersionTrackingException ex) {
			assertEquals(changelogTableDoesNotExistMessage , ex.getMessage());
		}
	}
	
	public void testShouldReturnEmptySetWhenTableHasNoRows() throws Exception {
		ensureTableDoesNotExist();
		createTable();

		assertEquals(0, databaseSchemaVersion.getAppliedChangeNumbers().size());
	}
	
	public void testCanRetrieveDeltaFragmentHeaderSql() throws Exception {
		ChangeScript script = new ChangeScript(3, "description"); 
		assertEquals("--------------- Fragment begins: #3 ---------------\nINSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (3, 'All', CURRENT_TIMESTAMP, USER, 'description');\nCOMMIT;\n", 
				databaseSchemaVersion.generateDoDeltaFragmentHeader(script));
	}
	
	public void testCanRetrieveDeltaFragmentFooterSql() throws Exception {
		ChangeScript script = new ChangeScript(3, "description"); 
		assertEquals("UPDATE changelog SET complete_dt = CURRENT_TIMESTAMP WHERE change_number = 3 AND delta_set = 'All';\nCOMMIT;\n--------------- Fragment ends: #3 ---------------\n", 
				databaseSchemaVersion.generateDoDeltaFragmentFooter(script));
	}

	private void ensureTableDoesNotExist() {
		try {
			executeSql("DROP TABLE " + DatabaseSchemaVersionManager.TABLE_NAME);
		} catch (Exception e) {
			// ignore
		}
	}
	
	protected void executeSql(String sql) throws SQLException {
		registerDbDriver();
		//DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
		Connection connection = DriverManager.getConnection(connectionString, username, password);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
	}

	protected abstract String getConnectionString();
	protected abstract String getUsername();
	protected abstract String getPassword();
	protected abstract String getDeltaSet();
	protected abstract String getChangelogTableDoesNotExistMessage();
	protected abstract void createTable() throws SQLException;
	protected abstract void insertRowIntoTable(int i) throws SQLException;
	protected abstract void registerDbDriver() throws SQLException;
}
