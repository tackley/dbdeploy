package com.dbdeploy.database;

import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.Controller;
import com.dbdeploy.appliers.TemplateBasedApplier;
import com.dbdeploy.database.changelog.ChangeLogTableCreator;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.ChangeScriptRepository;
import com.dbdeploy.scripts.StubChangeScript;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScriptGenerationTest {

    public static final List<String> DATABASE_SYNTAXES =
            Arrays.asList("hsql", "mssql", "mysql", "ora", "syb-ase", "db2", "pgsql", "derby", "ansi");

    @Test
	public void generateConsolidatedChangesScriptForAllDatabasesAndCompareAgainstTemplate() throws Exception {
		for (String syntax : DATABASE_SYNTAXES) {
			try {
				System.out.printf("Testing syntax %s\n", syntax);
				runIntegratedTestAndConfirmOutputResults(syntax, false);
				runIntegratedTestAndConfirmOutputResults(syntax, true);
			} catch (Exception e) {
				throw new Exception("Failed while testing syntax " + syntax, e);
			}
		}
	}

	private void runIntegratedTestAndConfirmOutputResults(String syntaxName, boolean withChangelog) throws Exception {

		StringWriter writer = new StringWriter();

		ChangeScript changeOne = new StubChangeScript(1, "001_change.sql", "-- contents of change script 1");
		ChangeScript changeTwo = new StubChangeScript(2, "002_change.sql", "-- contents of change script 2");

		List<ChangeScript> changeScripts = Arrays.asList(changeOne, changeTwo);
		ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(changeScripts);



		final StubSchemaManager schemaManager = new StubSchemaManager();
        QueryStatementSplitter qss = new QueryStatementSplitter();
        qss.setDelimiterType(DelimiterType.normal);

        ChangeLogTableCreator clCreator = null;
        if (withChangelog) {
            clCreator = new ChangeLogTableCreator("changelog", syntaxName, qss, null);
        }

		ChangeScriptApplier applier = new TemplateBasedApplier(writer, syntaxName, "changelog", clCreator, qss, null);
		Controller controller = new Controller(changeScriptRepository, schemaManager, applier, null);

		controller.processChangeScripts(Long.MAX_VALUE);

		assertEquals(readExpectedFileContents(getExpectedFilename(syntaxName, withChangelog)), writer.toString());
	}

	private String getExpectedFilename(String dbSyntaxName, boolean withChangelog) {
		return dbSyntaxName + "_expected" + (withChangelog ? "_changelog.sql" : ".sql");
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
		public StubSchemaManager() {
			super(null, "changelog");
		}

		@Override
		public List<Long> getAppliedChanges() throws SchemaVersionTrackingException {
			return Collections.emptyList();
		}
	}
}
