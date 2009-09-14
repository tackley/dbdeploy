package com.dbdeploy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import com.dbdeploy.database.DelimiterType;

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
				"--changeLogTableName my-change-log " +
				"--dbms ora " +
				"--delimiter \\ --delimitertype row").split(" "), dbDeploy);

		assertThat(dbDeploy.getUserid(), is("userid"));
		assertThat(dbDeploy.getPassword(), is("password"));
		assertThat(dbDeploy.getDriver(), is("a.b.c"));
		assertThat(dbDeploy.getUrl(), is("b:c:d"));
		assertThat(dbDeploy.getScriptdirectory().getName(), is("."));
		assertThat(dbDeploy.getOutputfile().getName(), is("output.sql"));
		assertThat(dbDeploy.getDbms(), is("ora"));
		assertThat(dbDeploy.getChangeLogTableName(), is("my-change-log"));
		assertThat(dbDeploy.getDelimiter(), is("\\"));
		assertThat(dbDeploy.getDelimiterType(), is(DelimiterType.row));
	}

}

