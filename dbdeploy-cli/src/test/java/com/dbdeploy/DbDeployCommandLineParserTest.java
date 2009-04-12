package com.dbdeploy;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import com.dbdeploy.DbDeploy;
import com.dbdeploy.DbDeployCommandLineParser;

public class DbDeployCommandLineParserTest {
	private final DbDeploy dbDeploy = new DbDeploy();
	private final DbDeployCommandLineParser parser = new DbDeployCommandLineParser();

	@Test
	public void canParseUserIdFromCommandLine() throws Exception {
		parser.parse("-U myuserid".split(" "), dbDeploy);
		assertEquals("myuserid", dbDeploy.getUserid());
	}

	@Test
	public void thisIsntReallyATestBecuaseThereIsNoAssertButItsVeryUsefulToLookAtTheResult() throws Exception {
		parser.printUsage();
	}

	@Test
	public void checkAllOfTheOtherFieldsParseOkHere() throws Exception {
		parser.parse(("-U userid -Ppassword --driver a.b.c --url b:c:d " +
				"--scriptdirectory . -o output.sql " +
				"--dbms ora --deltaset Schema").split(" "), dbDeploy);

		assertThat(dbDeploy.getUserid(), is("userid"));
		assertThat(dbDeploy.getPassword(), is("password"));
		assertThat(dbDeploy.getDriver(), is("a.b.c"));
		assertThat(dbDeploy.getUrl(), is("b:c:d"));
		assertThat(dbDeploy.getScriptdirectory().getName(), is("."));
		assertThat(dbDeploy.getOutputfile().getName(), is("output.sql"));
		assertThat(dbDeploy.getDbms(), is("ora"));
		assertThat(dbDeploy.getDeltaset(), is("Schema"));
	}

}

