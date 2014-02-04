package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.StubChangeScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnit44Runner.class)
public class FakeDirectToDbApplierTest {
	@Mock private QueryExecuter queryExecuter;
	@Mock private DatabaseSchemaVersionManager schemaVersionManager;
    @Mock private QueryStatementSplitter splitter;
	private DirectToDbApplier applier;

	@Before
	public void setUp() {
		applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter, true);
	}


	@Test
	public void shouldNotApplyScripts() throws Exception {
        List<ChangeScript> changes = new ArrayList<ChangeScript> ();
        ChangeScript changeScript = new StubChangeScript(1, "script", "content");
        changes.add(changeScript);

        when(splitter.split("content")).thenReturn(Arrays.asList("content"));

        applier.apply(changes);

        verify(queryExecuter, never()).execute(anyString());
        verify(schemaVersionManager).recordScriptApplied(changeScript);
	}

}

