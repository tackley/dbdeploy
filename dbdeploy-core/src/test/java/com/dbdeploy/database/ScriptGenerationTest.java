package com.dbdeploy.database;

import com.dbdeploy.Controller;
import com.dbdeploy.appliers.ChangeScriptApplier;
import com.dbdeploy.appliers.TemplateBasedApplier;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.ChangeScriptRepository;
import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScriptGenerationTest {

	@Test
	public void generateConsolidatedChangesScriptForAllDatabasesAndCompareAgainstTemplate() throws Exception {
		for (String syntax : Arrays.asList("hsql", "mssql", "mysql", "ora", "syb-ase", "db2", "pgsql")) {
			try {
				System.out.printf("Testing syntax %s\n", syntax);
				runIntegratedTestAndConfirmOutputResults(syntax);
			} catch (Exception e) {
				throw new RuntimeException("Failed while testing syntax " + syntax, e);
			}
		}
	}

	private void runIntegratedTestAndConfirmOutputResults(String syntaxName) throws Exception {

	    File outputfile = new File("outputfile");
	    outputfile.deleteOnExit();
	    
		ChangeScript changeOne = new StubChangeScript(1, "001_change.sql", "-- contents of change script 1");
		ChangeScript changeTwo = new StubChangeScript(2, "002_change.sql", "-- contents of change script 2");

		List<ChangeScript> changeScripts = Arrays.asList(changeOne, changeTwo);
		ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(changeScripts);

		final StubSchemaManager schemaManager = new StubSchemaManager();
		ChangeScriptApplier applier = new TemplateBasedApplier(outputfile, syntaxName, "changelog", null);
		Controller controller = new Controller(changeScriptRepository, schemaManager, applier);

		controller.processChangeScripts(Integer.MAX_VALUE);
		
		String outputfileContent = FileUtils.readFileToString(outputfile); 
        assertEquals(readExpectedFileContents(getExpectedFilename(syntaxName)), outputfileContent);
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
		public StubSchemaManager() {
			super(null, "changelog");
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
