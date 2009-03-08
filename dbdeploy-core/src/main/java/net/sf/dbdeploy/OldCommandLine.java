package net.sf.dbdeploy;

import net.sf.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.changelog.QueryExecuter;
import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import net.sf.dbdeploy.scripts.DirectoryScanner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class OldCommandLine {

	// TODO: using a properties file as a command line arg is bizzare.
	//  Pass all the stuff needed as real command line args, and use
	//  something like commons-cli to parse it
	public static void main(String[] args) {


		try {

			if (args.length != 1) {
				System.err.println("usage: dbdeploy propertyfilename");
				System.exit(3);
			}

			Properties properties = new Properties();
			properties.load(new FileInputStream(args[0]));

			String url = properties.getProperty("db.location");
			String userid = properties.getProperty("db.user");
			String password = properties.getProperty("db.password");
			String driver = properties.getProperty("db.driver");
			String dbms = properties.getProperty("db.dbms");
			String deltaSet = properties.getProperty("db.deltaSet");

			Class.forName(driver);

			DbmsSyntax dbmsSyntax = DbmsSyntax.createFor(dbms);

			QueryExecuter queryExecuter = new QueryExecuter(url, userid, password);

			DatabaseSchemaVersionManager databaseSchemaVersion =
					new DatabaseSchemaVersionManager(deltaSet, dbmsSyntax, queryExecuter);

			ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(new DirectoryScanner().getChangeScriptsForDirectory(new File(".")));

			new ToPrintSteamDeployer(databaseSchemaVersion, changeScriptRepository, System.out, dbmsSyntax, null).doDeploy(Integer.MAX_VALUE);


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