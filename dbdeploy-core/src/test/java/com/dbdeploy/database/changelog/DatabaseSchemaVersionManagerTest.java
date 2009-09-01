package com.dbdeploy.database.changelog;

import com.dbdeploy.database.syntax.DbmsSyntax;
import com.dbdeploy.scripts.ChangeScript;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasItems;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseSchemaVersionManagerTest {
	private DatabaseSchemaVersionManager schemaVersionManager;

	@Mock private ResultSet expectedResultSet;
	@Mock private QueryExecuter queryExecuter;

	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);

		when(queryExecuter.execute(anyString(), anyString())).thenReturn(expectedResultSet);

		schemaVersionManager
			= new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), queryExecuter, "changelog");

	}

	@Test
	public void shouldUseQueryExecuterToReadInformationFromTheChangelogTable() throws Exception {
		when(expectedResultSet.next()).thenReturn(true, true, true, false);
		when(expectedResultSet.getInt(1)).thenReturn(5, 9, 12);

		final List<Integer> numbers = schemaVersionManager.getAppliedChanges();
		assertThat(numbers, hasItems(5, 9, 12));
	}


	@Test
	public void shouldGenerateSqlStringToUpdateChangelogTableAfterScriptApplication() throws Exception {
		final ChangeScript script = new ChangeScript(99, "Some Description");
		String sql = schemaVersionManager.getChangelogUpdateSql(script);
		String expected =
				"INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) " +
						"VALUES (99, 'deltaSetName', (timestamp), (user), 'Some Description')";
		assertThat(sql, equalToIgnoringWhiteSpace(expected));
	}

	@Test
	public void shouldGenerateSqlStringToDeleteChangelogTableAfterUndoScriptApplication() throws Exception {
		final ChangeScript script = new ChangeScript(99, "Some Description");
		String sql = schemaVersionManager.getChangelogDeleteSql(script);
		String expected =
				"DELETE FROM changelog WHERE change_number = 99 and delta_set = 'deltaSetName'";
		assertThat(sql, equalToIgnoringWhiteSpace(expected));
	}
    
    @Test
    public void shouldGenerateSqlStringContainingSpecifiedChangelogTableNameOnUpdate() {
        DatabaseSchemaVersionManager schemaVersionManagerWithDifferentTableName =
                new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), queryExecuter,
                        "user_specified_changelog");

        final ChangeScript script = new ChangeScript(99, "Some Description");
        String updateSql = schemaVersionManagerWithDifferentTableName.getChangelogUpdateSql(script);

        assertThat(updateSql, Matchers.startsWith("INSERT INTO user_specified_changelog "));
    }

    @Test
    public void shouldGetAppliedChangesFromSpecifiedChangelogTableName() throws SQLException {
        DatabaseSchemaVersionManager schemaVersionManagerWithDifferentTableName =
                new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), queryExecuter,
                        "user_specified_changelog");

        schemaVersionManagerWithDifferentTableName.getAppliedChanges();

        verify(queryExecuter).execute(startsWith("SELECT change_number FROM user_specified_changelog "), anyString());
    }

    @Test
    public void shouldGenerateSqlStringContainingSpecifiedChangelogTableNameOnDelete() {
        DatabaseSchemaVersionManager schemaVersionManagerWithDifferentTableName =
                new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), queryExecuter,
                        "user_specified_changelog");

        final ChangeScript script = new ChangeScript(99, "Some Description");
        String updateSql = schemaVersionManagerWithDifferentTableName.getChangelogDeleteSql(script);

        assertThat(updateSql, Matchers.startsWith("DELETE FROM user_specified_changelog "));
    }

	private class StubDbmsSyntax extends DbmsSyntax {
		public String generateTimestamp() {
			return "(timestamp)";
		}

		public String generateUser() {
			return "(user)";
		}
	}

}

