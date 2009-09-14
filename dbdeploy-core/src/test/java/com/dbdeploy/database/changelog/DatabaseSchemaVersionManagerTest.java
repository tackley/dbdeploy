package com.dbdeploy.database.changelog;

import com.dbdeploy.scripts.ChangeScript;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class DatabaseSchemaVersionManagerTest {
    private final ChangeScript script = new ChangeScript(99, "Some Description");

    private DatabaseSchemaVersionManager schemaVersionManager;

    @Mock private ResultSet expectedResultSet;
    @Mock private QueryExecuter queryExecuter;
    @Mock private DatabaseSchemaVersionManager.CurrentTimeProvider timeProvider;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);

        when(queryExecuter.executeQuery(anyString())).thenReturn(expectedResultSet);

        schemaVersionManager = new DatabaseSchemaVersionManager(queryExecuter, "changelog");
        schemaVersionManager.setTimeProvider(timeProvider);
    }

    @Test
    public void shouldUseQueryExecuterToReadInformationFromTheChangelogTable() throws Exception {
        when(expectedResultSet.next()).thenReturn(true, true, true, false);
        when(expectedResultSet.getInt(1)).thenReturn(5, 9, 12);

        final List<Integer> numbers = schemaVersionManager.getAppliedChanges();
        assertThat(numbers, hasItems(5, 9, 12));
    }


    @Test
    public void shouldUpdateChangelogTable() throws Exception {
        Date now = new Date();

        when(queryExecuter.getDatabaseUsername()).thenReturn("DBUSER");
        when(timeProvider.now()).thenReturn(now);

        schemaVersionManager.recordScriptApplied(script);
        String expected =
                "INSERT INTO changelog (change_number, complete_dt, applied_by, description) " +
                        "VALUES (?, ?, ?, ?)";

        verify(queryExecuter).execute(expected, script.getId(),
                new Timestamp(now.getTime()), "DBUSER", script.getDescription());
    }

    @Test
    public void shouldGenerateSqlStringToDeleteChangelogTableAfterUndoScriptApplication() throws Exception {
        String sql = schemaVersionManager.getChangelogDeleteSql(script);
        String expected =
                "DELETE FROM changelog WHERE change_number = 99";
        assertThat(sql, equalToIgnoringWhiteSpace(expected));
    }

    @Test
    public void shouldGetAppliedChangesFromSpecifiedChangelogTableName() throws SQLException {
        DatabaseSchemaVersionManager schemaVersionManagerWithDifferentTableName =
                new DatabaseSchemaVersionManager(queryExecuter,
                        "user_specified_changelog");

        schemaVersionManagerWithDifferentTableName.getAppliedChanges();

        verify(queryExecuter).executeQuery(startsWith("SELECT change_number FROM user_specified_changelog "));
    }

    @Test
    public void shouldGenerateSqlStringContainingSpecifiedChangelogTableNameOnDelete() {
        DatabaseSchemaVersionManager schemaVersionManagerWithDifferentTableName =
                new DatabaseSchemaVersionManager(queryExecuter,
                        "user_specified_changelog");

        String updateSql = schemaVersionManagerWithDifferentTableName.getChangelogDeleteSql(script);

        assertThat(updateSql, Matchers.startsWith("DELETE FROM user_specified_changelog "));
    }
}

