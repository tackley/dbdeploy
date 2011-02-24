package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.exceptions.ChangeScriptFailedException;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.StubChangeScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import java.sql.SQLException;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnit44Runner.class)
public class DirectToDbApplierTest {
	@Mock private QueryExecuter queryExecuter;
	@Mock private DatabaseSchemaVersionManager schemaVersionManager;
    @Mock private QueryStatementSplitter splitter;
	private DirectToDbApplier applier;

	@Before
	public void setUp() {
		applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter);
	}
	
	@Test
	public void shouldSetConnectionToManualCommitModeAtStart() throws Exception {
		applier.begin();

		verify(queryExecuter).setAutoCommit(false);
	}

	@Test
	public void shouldApplyChangeScriptBySplittingContentUsingTheSplitter() throws Exception {
        when(splitter.split("split; content")).thenReturn(Arrays.asList("split", "content"));

		applier.applyChangeScript(new StubChangeScript(1, "script", "split; content"));
		
		verify(queryExecuter).execute("split");
		verify(queryExecuter).execute("content");
	}

	@Test
	public void shouldRethrowSqlExceptionsWithInformationAboutWhatStringFailed() throws Exception {
		when(splitter.split("split; content")).thenReturn(Arrays.asList("split", "content"));
		ChangeScript script = new StubChangeScript(1, "script", "split; content");

		doThrow(new SQLException("dummy exception")).when(queryExecuter).execute("split");

		try {
			applier.applyChangeScript(script);
			fail("exception expected");
		} catch (ChangeScriptFailedException e) {
			assertThat(e.getExecutedSql(), is("split"));
			assertThat(e.getScript(), is(script));
		}

		verify(queryExecuter, never()).execute("content");
	}

	@Test
	public void shouldInsertToSchemaVersionTable() throws Exception {
		ChangeScript changeScript = new ChangeScript(1, "script.sql");

		applier.insertToSchemaVersionTable(changeScript);

        verify(schemaVersionManager).recordScriptApplied(changeScript);

	}

	@Test
	public void shouldCommitTransactionOnErrrCommitTransaction() throws Exception {
		applier.commitTransaction();

		verify(queryExecuter).commit();
	}


}
