package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.exceptions.ChangeScriptApplierException;
import com.dbdeploy.exceptions.ChangeScriptFailedException;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.StubChangeScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnit44Runner.class)
public class DirectToDbApplierTest {
	@Mock private QueryExecuter queryExecuter;
	@Mock private DatabaseSchemaVersionManager schemaVersionManager;
    @Mock private QueryStatementSplitter splitter;
	private DirectToDbApplier applier;

	@Before
	public void setUp() {

        applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter, new ArrayList<String>());
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

    @Test
    public void shouldContinueOnErrorIfErrorToIgnoreAreProvided() throws Exception
    {
        applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter, Arrays.asList("ORA-0001: unique constraint"));

        when(splitter.split("split1; content1")).thenReturn(Arrays.asList("split1", "content1"));
        when(splitter.split("content2")).thenReturn(Arrays.asList("content2"));

        ChangeScript script1 = new StubChangeScript(1, "script", "split1; content1");
        ChangeScript script2 = new StubChangeScript(2, "script", "content2");

        doThrow(new SQLException("ORA-0001: unique constraint")).when(queryExecuter).execute("split1");

        ChangeScriptApplierException expectedException = null;
        try {
            applier.apply(Arrays.asList(script1, script2));

        } catch (ChangeScriptApplierException e) {
            expectedException = e;
        }

        verify(queryExecuter).execute("split1");
        verify(queryExecuter).execute("content2");

        ChangeScriptFailedException scriptException = expectedException.getChangeScriptExceptions().get(0);
        assertThat(scriptException .getExecutedSql(), is("split1"));
        assertThat(scriptException .getScript(), is(script1));
    }

    @Test
    public void shouldStopExecutionIfExceptionIsOtherThanIgnored() throws SQLException {
        applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter, Arrays.asList("ORA-0001: unique constraint"));

        when(splitter.split("split1; content1")).thenReturn(Arrays.asList("split1", "content1"));
        when(splitter.split("content2")).thenReturn(Arrays.asList("content2"));

        ChangeScript script1 = new StubChangeScript(1, "script", "split1; content1");
        ChangeScript script2 = new StubChangeScript(2, "script", "content2");

        doThrow(new SQLException("ORA-0002: table or view does not exist")).when(queryExecuter).execute("split1");

        ChangeScriptApplierException expectedException = null;
        try {
            applier.apply(Arrays.asList(script1, script2));

        } catch (ChangeScriptApplierException e) {
            expectedException = e;
        }

        assertNotNull(expectedException);
        verify(queryExecuter).execute("split1");
        verify(queryExecuter, times(0)).execute("content2");

    }



    @Test
    public void shouldStopExecutionAndGiveAllPreviousExceptionsIfToIgnoreAreProvided() throws SQLException {
        applier = new DirectToDbApplier(queryExecuter, schemaVersionManager, splitter, Arrays.asList("ORA-0001: unique constraint"));

        when(splitter.split("split1; content1")).thenReturn(Arrays.asList("split1", "content1"));
        when(splitter.split("content2")).thenReturn(Arrays.asList("content2"));

        ChangeScript script1 = new StubChangeScript(1, "script", "split1; content1");
        ChangeScript script2 = new StubChangeScript(2, "script", "content2");

        doThrow(new SQLException("ORA-0001: unique constraint")).when(queryExecuter).execute("split1");
        doThrow(new SQLException("ORA-0002: table or view does not exist")).when(queryExecuter).execute("content2");

        ChangeScriptApplierException expectedException = null;
        try {
            applier.apply(Arrays.asList(script1, script2));

        } catch (ChangeScriptApplierException e) {
            expectedException = e;
        }

        assertNotNull(expectedException);
        ChangeScriptFailedException scriptException = expectedException.getChangeScriptExceptions().get(0);
        assertThat(scriptException .getExecutedSql(), is("split1"));
        assertThat(scriptException.getScript(), is(script1));

        scriptException = expectedException.getChangeScriptExceptions().get(1);
        assertThat(scriptException .getExecutedSql(), is("content2"));
        assertThat(scriptException.getScript(), is(script2));
    }
}
