package net.tackley.dbdeploy;

import java.sql.DriverManager;

import net.tackley.dbdeploy.database.DatabaseSchemaVersion;

public class CommandLine {

	public static void main(String[] args) {

		try {

			DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

			Output output = new Output();
			DatabaseSchemaVersion databaseSchemaVersion = new DatabaseSchemaVersion(null, null, null);

			Controller controller = new Controller(output, databaseSchemaVersion, null, null);
			controller.applyScriptToGetChangesUpToLatestVersion();

		} catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}
}
