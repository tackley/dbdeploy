package com.dbdeploy.database.changelog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the functionality of {@link EmptyDatabaseSchemaVersionManager}
 */
public class EmptyDatabaseSchemaVersionManagerTest {
    @Mock QueryExecuter queryExecuter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldIdentifyWhenChangelogTableNotPresent() throws SQLException {
        EmptyDatabaseSchemaVersionManager schemaVersionManagerWithoutChangelog =
                new EmptyDatabaseSchemaVersionManager(queryExecuter, "test_changelog");
        when(queryExecuter.doesTableExist("test_changelog")).thenReturn(false);

        assertThat(schemaVersionManagerWithoutChangelog.getAppliedChanges().size(), is(0));

        verify(queryExecuter).doesTableExist("test_changelog");
        verify(queryExecuter, never()).execute(Mockito.any(String.class));
    }
}
