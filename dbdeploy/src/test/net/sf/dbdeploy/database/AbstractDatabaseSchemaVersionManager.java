package net.sf.dbdeploy.database;

import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class AbstractDatabaseSchemaVersionManager {
	
	private DatabaseSchemaVersionManager databaseSchemaVersion;
	private String connectionString = getConnectionString();
	private String username = getUsername();
	private String password = getPassword();
	private String deltaSet = getDeltaSet();
	private String changelogTableDoesNotExistMessage = getChangelogTableDoesNotExistMessage();
	
	@Before
	protected void setUp() throws Exception {
		registerDbDriver();

		databaseSchemaVersion = new DatabaseSchemaVersionManager(connectionString, username, password, new OracleDbmsSyntax(), deltaSet);
	}

	@Test
	public void canRetrieveSchemaVersionFromDatabase() throws Exception {
		ensureTableDoesNotExist();
		createTable();
		insertRowIntoTable(5);

		List<Integer> appliedChangeNumbers = databaseSchemaVersion.getAppliedChangeNumbers();
		assertEquals(1, appliedChangeNumbers.size());
		assertThat(5, isIn(appliedChangeNumbers));
	}
	

	@Test
	public void throwsWhenDatabaseTableDoesNotExist() throws Exception {
		ensureTableDoesNotExist();
		
		try {
			databaseSchemaVersion.getAppliedChangeNumbers();
			fail("expected exception");
		} catch (SchemaVersionTrackingException ex) {
			assertEquals(changelogTableDoesNotExistMessage , ex.getMessage());
		}
	}

	@Test
	public void shouldReturnEmptySetWhenTableHasNoRows() throws Exception {
		ensureTableDoesNotExist();
		createTable();

		assertEquals(0, databaseSchemaVersion.getAppliedChangeNumbers().size());
	}

	@Test
	public void canRetrieveDeltaFragmentHeaderSql() throws Exception {
		ChangeScript script = new ChangeScript(3, "description"); 
		assertEquals("--------------- Fragment begins: #3 ---------------\nINSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (3, 'All', CURRENT_TIMESTAMP, USER, 'description');\nCOMMIT;\n", 
				databaseSchemaVersion.generateDoDeltaFragmentHeader(script));
	}

	@Test
	public void canRetrieveDeltaFragmentFooterSql() throws Exception {
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
