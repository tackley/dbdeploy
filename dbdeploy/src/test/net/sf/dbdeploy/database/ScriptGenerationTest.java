package net.sf.dbdeploy.database;

import net.sf.dbdeploy.ToPrintSteamDeployer;
import net.sf.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.database.syntax.DbmsSyntaxFactory;
import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import static org.junit.Assert.*;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(Theories.class)
public class ScriptGenerationTest {

	@DataPoint
	public static String ORACLE_SQLPLUS = "ora-sqlplus";

	@DataPoint
	public static String ORACLE = "ora";

	@DataPoint
	public static String HSQL = "hsql";

	@DataPoint
	public static String SYB_ASE = "syb-ase";

	@DataPoint
	public static String MSSQL = "mssql";

	@DataPoint
	public static String MYSQL = "mysql";
	
	@Theory
	public void runIntegratedTestAndConfirmOutputResults(String syntaxName) throws Exception {
		DbmsSyntax syntax = getSyntax(syntaxName);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream);

		ChangeScript changeOne = new StubChangeScript(1, "1: change1.sql", "-- contents of change script 1");
		ChangeScript changeTwo = new StubChangeScript(2, "2: change2.sql", "-- contents of change script 2");

		List<ChangeScript> changeScripts = Arrays.asList(changeOne, changeTwo);
		ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(changeScripts);

		ToPrintSteamDeployer deployer = new ToPrintSteamDeployer(
				new StubSchemaManager(syntax), changeScriptRepository, printStream,
				syntax, null);

		deployer.doDeploy(Integer.MAX_VALUE);

		org.junit.Assert.assertEquals(readExpectedFileContents(getExpectedFilename(syntaxName)), outputStream.toString());
	}

	private DbmsSyntax getSyntax(String dbSyntaxName) {
		return new DbmsSyntaxFactory(dbSyntaxName).createDbmsSyntax();
	}

	private String getExpectedFilename(String dbSyntaxName) {
		return dbSyntaxName + "_expected.sql";
	}

	private String readExpectedFileContents(String expectedFilename) throws IOException {
		final InputStream stream = getClass().getResourceAsStream(expectedFilename);
		assertThat("did not find resource file called " + expectedFilename, stream, org.hamcrest.Matchers.notNullValue());
		int size = stream.available();
		byte[] content = new byte[size];
		org.junit.Assert.assertThat(stream.read(content), org.hamcrest.Matchers.is(size));
		return new String(content);
	}


	private class StubSchemaManager extends DatabaseSchemaVersionManager {
		public StubSchemaManager(DbmsSyntax syntax) {
			super(null, null, null, syntax, null);
		}

		public List<Integer> getAppliedChangeNumbers() throws SchemaVersionTrackingException {
			return Collections.emptyList();
		}
	}

	private class StubChangeScript extends ChangeScript {
		private final String changeContents;

		public StubChangeScript(int changeNumber, String description, String changeContents) {
			super(changeNumber, description);
			this.changeContents = changeContents;
		}

		public String getContent() throws IOException {
			return changeContents;
		}
	}
}
