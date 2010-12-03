package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.scripts.ChangeScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnit44Runner;

import java.util.Arrays;

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
        when(splitter.split("content")).thenReturn(Arrays.asList("split", "content"));

		applier.applyChangeScriptContent("content");
		
		verify(queryExecuter).execute("split");
		verify(queryExecuter).execute("content");
	}

	@Test
	public void shouldInsertToSchemaVersionTable() throws Exception {
		ChangeScript changeScript = new ChangeScript(1, "script.sql");

		applier.insertToSchemaVersionTable(changeScript, ApplyMode.DO);

        verify(schemaVersionManager).recordScriptApplied(changeScript);

	}

	@Test
	public void shouldCommitTransactionOnErrrCommitTransaction() throws Exception {
		applier.commitTransaction();

		verify(queryExecuter).commit();
	}


}
