package net.sf.dbdeploy;

import net.sf.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.database.syntax.DbmsSyntaxFactory;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import net.sf.dbdeploy.scripts.DirectoryScanner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class CommandLine {

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
			
			DbmsSyntaxFactory factory = new DbmsSyntaxFactory(dbms);
			DbmsSyntax dbmsSyntax = factory.createDbmsSyntax();
			
			DatabaseSchemaVersionManager databaseSchemaVersion = 
				new DatabaseSchemaVersionManager(url, userid, password, dbmsSyntax, deltaSet);

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
