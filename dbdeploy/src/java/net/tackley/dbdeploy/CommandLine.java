package net.tackley.dbdeploy;

import java.io.File;
import java.sql.DriverManager;

import net.tackley.dbdeploy.database.DatabaseSchemaVersionManager;
import net.tackley.dbdeploy.exceptions.DbDeployException;
import net.tackley.dbdeploy.scripts.ChangeScriptRepositoryFactory;

public class CommandLine {
	static final String CONNECTION_STRING = "jdbc:oracle:thin:@localhost:1521:orcl";
	static final String USERNAME = "dbdeploy";
	static final String PASSWORD = "dbdeploy";

	public static void main(String[] args) {

		
		try {

			DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

			Output output = new Output();
			DatabaseSchemaVersionManager databaseSchemaVersion = new DatabaseSchemaVersionManager(CONNECTION_STRING, USERNAME, PASSWORD);
			ChangeScriptRepositoryFactory repositoryFactory = new ChangeScriptRepositoryFactory();
			ChangeScriptExecuter changeScriptExecuter = new ChangeScriptExecuter();

			Controller controller = new Controller(output, databaseSchemaVersion, repositoryFactory, 
					changeScriptExecuter);
			
			controller.applyScriptToGetChangesUpToLatestVersion(new File("."));

		} catch (DbDeployException ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			ex.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}
}
