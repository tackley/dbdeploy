package com.dbdeploy.appliers;

import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.syntax.DbmsSyntax;
import com.dbdeploy.scripts.ChangeScript;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PrintStreamApplierTest {
	private PrintStreamApplier applier;
	private ByteArrayOutputStream stringOutputStream;
	private PrintStreamApplierTest.StubDbmsSyntax dbmsSyntax = new StubDbmsSyntax();
	@Mock private DatabaseSchemaVersionManager schemaVersionManager;
	private ChangeScript script;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		stringOutputStream = new ByteArrayOutputStream();
		applier = new PrintStreamApplier(ApplyMode.DO, new PrintStream(stringOutputStream), dbmsSyntax, schemaVersionManager);
		script = new ChangeScript(2, "002_change_script_2.sql");
	}

	@Test
	public void shouldWriteOutScriptHeaderOnBegin() throws Exception {
		applier.begin();

		assertThat(getFileContents(), equalToIgnoringWhiteSpace(dbmsSyntax.generateScriptHeader()));

	}


	@Test
	public void shouldDumpOutMeaningfulHeaderForScripts() throws Exception {
		applier.preChangeScriptApply(script);

		final String expected = "----- START CHANGE SCRIPT #2: 002_change_script_2.sql -----";

		assertThat(getFileContents(), equalToIgnoringWhiteSpace(expected));
	}

	@Test
	public void shouldBeginTransactionUsingDatabaseSyntax() throws Exception {
		applier.beginTransaction();
		
		assertThat(getFileContents(), equalToIgnoringWhiteSpace(dbmsSyntax.generateBeginTransaction()));
	}

	@Test
	public void shouldAddContentOfChangeScript() throws Exception {
		applier.applyChangeScriptContent("some content");
		assertThat(getFileContents(), equalToIgnoringWhiteSpace("some content"));
	}


	@Test
	public void shouldCommitTransactionUsingDatabaseSyntax() throws Exception {
		applier.commitTransaction();

		assertThat(getFileContents(), equalToIgnoringWhiteSpace(dbmsSyntax.generateCommit()));
	}


	@Test
	public void shouldDumpOutMeaningfulTrailerForScripts() throws Exception {
		applier.postChangeScriptApply(script);

		final String expected = "----- END CHANGE SCRIPT #2: 002_change_script_2.sql -----";

		assertThat(getFileContents(), equalToIgnoringWhiteSpace(expected));
	}

	@Test
	public void shouldUseDatabaseSchemaVersionManagerToGenerateChangelogUpdateScript() {
		when(schemaVersionManager.getChangelogUpdateSql(script)).thenReturn("(sql to update changelog)");

		applier.insertToSchemaVersionTable(script);

		assertThat(getFileContents(), equalToIgnoringWhiteSpace("(sql to update changelog);"));
	}


	@Test
	public void shouldUseDatabaseSchemaVersionManagerToGenerateChangelogDeleteScript() {
		when(schemaVersionManager.getChangelogDeleteSql(script)).thenReturn("(sql to delete changelog)");

		applier.deleteFromSchemaVersionTable(script);

		assertThat(getFileContents(), equalToIgnoringWhiteSpace("(sql to delete changelog);"));
	}


	private String getFileContents() {
		return stringOutputStream.toString();
	}


	private class StubDbmsSyntax extends DbmsSyntax {
		@Override
		public String generateTimestamp() {
			return "TIMESTAMP";
		}

		@Override
		public String generateUser() {
			return "USER";
		}

		@Override
		public String generateScriptHeader() {
			return "/* SCRIPT HEADER */";
		}

		@Override
		public String generateStatementDelimiter() {
			return ";";
		}

		@Override
		public String generateCommit() {
			return "COMMIT TRANSACTION";
		}

		@Override
		public String generateBeginTransaction() {
			return "BEGIN TRANSACTION";
		}
	}
}
