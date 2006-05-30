package net.tackley.dbdeploy;

import java.io.File;
import java.sql.DriverManager;

import net.tackley.dbdeploy.database.DatabaseSchemaVersionManager;
import net.tackley.dbdeploy.exceptions.DbDeployException;
import net.tackley.dbdeploy.scripts.ChangeScriptRepositoryFactory;

public class CommandLine {

	public static void main(String[] args) {

		
		try {

			DriverManager.registerDriver (new oracle.jdbc.OracleDriver());

			if (args.length != 3) {
				System.err.println("usage: dbdeploy dbConnectionString username password");
				System.exit(3);
			}
			String connectionString = args[0];
			String username = args[1];
			String password = args[2];
			
			Output output = new Output();
			DatabaseSchemaVersionManager databaseSchemaVersion = new DatabaseSchemaVersionManager(connectionString, username, password);
			ChangeScriptRepositoryFactory repositoryFactory = new ChangeScriptRepositoryFactory();
			ChangeScriptExecuter changeScriptExecuter = new ChangeScriptExecuter(System.out);

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
