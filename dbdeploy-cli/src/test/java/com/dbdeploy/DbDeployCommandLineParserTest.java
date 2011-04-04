package com.dbdeploy;

import com.dbdeploy.database.DelimiterType;
import com.dbdeploy.database.LineEnding;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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
		assertThat(dbDeploy.getTemplatedir().getPath(), is(File.separator + "tmp" + File.separator + "mytemplates"));
	}

	@Test
	public void delimiterTypeWorksOk() throws Exception {
		parser.parse("--delimitertype normal".split(" "), dbDeploy);
		assertThat(dbDeploy.getDelimiterType(), is(DelimiterType.normal));

		parser.parse("--delimitertype row".split(" "), dbDeploy);
		assertThat(dbDeploy.getDelimiterType(), is(DelimiterType.row));
	}

	@Test
	public void lineEndingWorksOk() throws Exception {
		assertThat(dbDeploy.getLineEnding(), is(LineEnding.platform));

		parser.parse("--lineending cr".split(" "), dbDeploy);
		assertThat(dbDeploy.getLineEnding(), is(LineEnding.cr));

		parser.parse("--lineending crlf".split(" "), dbDeploy);
		assertThat(dbDeploy.getLineEnding(), is(LineEnding.crlf));

		parser.parse("--lineending lf".split(" "), dbDeploy);
		assertThat(dbDeploy.getLineEnding(), is(LineEnding.lf));

		parser.parse("--lineending platform".split(" "), dbDeploy);
		assertThat(dbDeploy.getLineEnding(), is(LineEnding.platform));

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

