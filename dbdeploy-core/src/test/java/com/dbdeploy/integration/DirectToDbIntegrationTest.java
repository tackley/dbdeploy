package com.dbdeploy.integration;

import com.dbdeploy.DbDeploy;
import com.dbdeploy.exceptions.ChangeScriptValidationFailedException;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

public class DirectToDbIntegrationTest {
	@Test
	public void shouldSuccessfullyApplyAValidSetOfDeltas() throws Exception {
		Database db = new Database("todb_success_test");
		db.createSchemaVersionTable();

		DbDeploy dbDeploy = new DbDeploy();
		db.applyDatabaseSettingsTo(dbDeploy);
		dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/deltas"));
		dbDeploy.go();

		assertThat(db.getChangelogEntries(), hasItems(1L, 2L));

		List<Object[]> results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(1));
		assertThat((Integer) results.get(0)[0], is(6));
	}

    @Test
    public void shouldSuccessfullyApplyAValidSetOfDeltasIncludingMutliStatementDeltas() throws Exception {
        Database db = new Database("todb_multistatement_test");
        db.createSchemaVersionTable();

        DbDeploy dbDeploy = new DbDeploy();
        db.applyDatabaseSettingsTo(dbDeploy);
        dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/multi_statement_deltas"));
        dbDeploy.go();

        assertThat(db.getChangelogEntries(), hasItems(1L, 2L));

        List<Object[]> results = db.executeQuery("select id from Test");
        assertThat(results.size(), is(2));
        assertThat(results, hasItems(new Object[] {6}, new Object[] {7}));
    }

    @Test
    public void shouldApplyValidateChangeScripts() throws Exception {
        Database db = new Database("todb_custaom_validator_passing_test");
        db.createSchemaVersionTable();

        DbDeploy dbDeploy = new DbDeploy();
        db.applyDatabaseSettingsTo(dbDeploy);
        dbDeploy.setChangeListValidatorProviderClassName("com.dbdeploy.integration.ChangeScriptValidatorProviderImpl");
        dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/validation_passing_deltas"));
        dbDeploy.go();

        assertThat(db.getChangelogEntries(), hasItems(20150920220022L));
    }


    @Test(expected = ChangeScriptValidationFailedException.class)
    public void shouldValidateChangeScriptsToBeAppliedProvidedByValidatorFactory() throws Exception {
        Database db = new Database("todb__custom_validator_failing_test");
        db.createSchemaVersionTable();

        DbDeploy dbDeploy = new DbDeploy();
        db.applyDatabaseSettingsTo(dbDeploy);
        dbDeploy.setChangeListValidatorProviderClassName("com.dbdeploy.integration.ChangeScriptValidatorProviderImpl");
        dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/validation_failing_deltas"));
        dbDeploy.go();
    }

	@Test
	public void shouldBeAbleToRecoverFromBadScriptsJustByRunningCorrectedScriptsAgain() throws Exception {
		Database db = new Database("todb_failure_recovery_test");
		db.createSchemaVersionTable();

		DbDeploy dbDeploy = new DbDeploy();
		db.applyDatabaseSettingsTo(dbDeploy);
		dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/invalid_deltas"));
		try {
			dbDeploy.go();
		} catch (Exception ex) {
			//expected
			assertThat(ex.getMessage(), containsString("Column count does not match in statement"));
		}

		// script 2 failed, so it should not be considered applied to the database
		assertThat(db.getChangelogEntries(), hasItems(1L));
		assertThat(db.getChangelogEntries(), not(hasItems(2L)));

		List<Object[]> results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(0));

		// now run dbdeploy again with valid scripts, should recover
		dbDeploy.setScriptdirectory(findScriptDirectory("src/it/db/deltas"));
		dbDeploy.go();

		assertThat(db.getChangelogEntries(), hasItems(1L, 2L));

		results = db.executeQuery("select id from Test");
		assertThat(results.size(), is(1));
	}

	private File findScriptDirectory(String directoryName) {
		File directoryWhenRunningUnderMaven = new File(directoryName);
		if (directoryWhenRunningUnderMaven.isDirectory()) {
			return directoryWhenRunningUnderMaven;
		}

		File directoryWhenRunningUnderIde = new File("dbdeploy-core", directoryName);
		if (directoryWhenRunningUnderIde.isDirectory()) {
			return directoryWhenRunningUnderIde;
		}

		fail("Could not find script directory: " + directoryName);

		return null;
	}

}