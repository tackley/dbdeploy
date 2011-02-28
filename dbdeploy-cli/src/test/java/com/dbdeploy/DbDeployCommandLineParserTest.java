package com.dbdeploy;

import com.dbdeploy.database.DelimiterType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DbDeployCommandLineParserTest {
    UserInputReader userInputReader = mock(UserInputReader.class);

	private final DbDeploy dbDeploy = new DbDeploy();
	private final DbDeployCommandLineParser parser = new DbDeployCommandLineParser(userInputReader);

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
				"--templatedir /tmp/mytemplates " +
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
		assertThat(dbDeploy.getTemplatedir().getPath(), is("/tmp/mytemplates"));
	}

    @Test
    public void shouldPromptFromStdinForPasswordIfPasswordParamSuppliedWithNoArg() throws Exception {
        when(userInputReader.read("Password")).thenReturn("user entered password");

        parser.parse(new String[] { "-P" }, dbDeploy);

        assertThat(dbDeploy.getPassword(), is("user entered password"));
    }

    @Test
    public void shouldNotPromptForPasswordWhenSupplied() throws Exception {
        parser.parse(new String[]{"-P", "password"}, dbDeploy);
        verifyZeroInteractions(userInputReader);
    }

    @Test
    public void shouldNotPromptForPasswordNotSpecifiedOnCommandLine() throws Exception {
        // this is important: not all databases require passwords :)
        parser.parse(new String[] {}, dbDeploy);
        verifyZeroInteractions(userInputReader);
    }

}

