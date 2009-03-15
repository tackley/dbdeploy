package net.sf.dbdeploy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.sql.SQLException;

public class IntegrationTests {
	@Test
	public void shouldSuccessfullyApplyAValidSetOfDeltas() throws Exception {
		Database db = new Database("success_test");
		db.createSchemaVersionTable();

		File outputFile = File.createTempFile("success",".sql");

		DbDeploy dbDeploy = new DbDeploy();
		db.applyDatabaseSettingsTo(dbDeploy);
		dbDeploy.setScriptdirectory(new File("src/it/db/deltas"));
		dbDeploy.setOutputfile(outputFile);
		dbDeploy.go();

		db.applyScript(outputFile);

		assertThat(db.getChangelogEntries(), hasItems(1, 2));

		List<Object[]> results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(1));
		assertThat((Integer) results.get(0)[0], is(6));
	}

	@Test
	public void shouldBeAbleToRecoverFromBadScriptsJustByRunningCorrectedScriptsAgain() throws Exception {
		File outputFile = File.createTempFile("recovery",".sql");

		Database db = new Database("failure_recovery_test");
		db.createSchemaVersionTable();

		DbDeploy dbDeploy = new DbDeploy();
		db.applyDatabaseSettingsTo(dbDeploy);
		dbDeploy.setScriptdirectory(new File("src/it/db/invalid_deltas"));
		dbDeploy.setOutputfile(outputFile);
		dbDeploy.go();

		try {
			db.applyScript(outputFile);
		} catch (SQLException ex) {
			//expected
			assertThat(ex.getMessage(), startsWith("Column count does not match in statement"));
		}

		// script 2 failed, so it should not be considered applied to the database
		assertThat(db.getChangelogEntries(), hasItems(1));
		assertThat(db.getChangelogEntries(), not(hasItems(2)));

		List<Object[]> results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(0));

		assertThat(db.getChangelogEntries(), hasItems(1));
		// now run dbdeploy again with valid scripts, should recover
		dbDeploy.setScriptdirectory(new File("src/it/db/deltas"));
		dbDeploy.setOutputfile(outputFile);
		dbDeploy.go();

		db.applyScript(outputFile);

		results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(1));
	}

}
