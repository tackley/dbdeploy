package com.dbdeploy.database;

import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.Controller;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.appliers.ApplyMode;
import com.dbdeploy.appliers.PrintStreamApplier;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.syntax.DbmsSyntax;
import com.dbdeploy.database.syntax.DbmsSyntaxFactory;
import com.dbdeploy.scripts.ChangeScriptRepository;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScriptGenerationTest {
	// TODO: need to test undo scripts as well as do scripts

	@Test
	public void generateConsolidatedChangesScriptForAllDatabasesAndCompareAgainstTemplate() throws Exception {
		for (String syntax : new DbmsSyntaxFactory().getSyntaxNames()) {
			try {
				System.out.printf("Testing syntax %s\n", syntax);
				runIntegratedTestAndConfirmOutputResults(syntax);
			} catch (Exception e) {
				throw new RuntimeException("Failed while testing syntax " + syntax, e);
			}
		}
	}

	private void runIntegratedTestAndConfirmOutputResults(String syntaxName) throws Exception {
		DbmsSyntax syntax = DbmsSyntax.createFor(syntaxName);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream);

		ChangeScript changeOne = new StubChangeScript(1, "001_change.sql", "-- contents of change script 1");
		ChangeScript changeTwo = new StubChangeScript(2, "002_change.sql", "-- contents of change script 2");

		List<ChangeScript> changeScripts = Arrays.asList(changeOne, changeTwo);
		ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(changeScripts);

		final StubSchemaManager schemaManager = new StubSchemaManager(syntax);
		ChangeScriptApplier applier = new PrintStreamApplier(ApplyMode.DO, printStream, syntax, schemaManager);
		Controller controller = new Controller(changeScriptRepository, schemaManager,
				applier, null);

		controller.processChangeScripts(Integer.MAX_VALUE);

		assertEquals(readExpectedFileContents(getExpectedFilename(syntaxName)), outputStream.toString());
	}

	private String getExpectedFilename(String dbSyntaxName) {
		return dbSyntaxName + "_expected.sql";
	}

	private String readExpectedFileContents(String expectedFilename) throws IOException {
		final InputStream stream = getClass().getResourceAsStream(expectedFilename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			return readEntireStreamIntoAStringWithConversionToSystemDependantLineTerminators(reader);
		} finally {
			reader.close();
		}
	}

	private String readEntireStreamIntoAStringWithConversionToSystemDependantLineTerminators(BufferedReader reader) throws IOException {
		StringWriter contentWithSystemDependentLineTerminators = new StringWriter();
		PrintWriter newLineConvertingContentWriter = new PrintWriter(contentWithSystemDependentLineTerminators);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				newLineConvertingContentWriter.println(line);
			}
			newLineConvertingContentWriter.flush();
			return contentWithSystemDependentLineTerminators.toString();
		} finally {
			newLineConvertingContentWriter.close();
		}
	}


	private class StubSchemaManager extends DatabaseSchemaVersionManager {
		public StubSchemaManager(DbmsSyntax syntax) {
			super(null, syntax, null);
		}

		@Override
		public List<Integer> getAppliedChanges() throws SchemaVersionTrackingException {
			return Collections.emptyList();
		}
	}

	private class StubChangeScript extends ChangeScript {
		private final String changeContents;

		public StubChangeScript(int changeNumber, String description, String changeContents) {
			super(changeNumber, description);
			this.changeContents = changeContents;
		}

		@Override
		public String getContent() {
			return changeContents;
		}
	}
}
