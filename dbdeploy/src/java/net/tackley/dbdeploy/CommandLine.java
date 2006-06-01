package net.tackley.dbdeploy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import net.tackley.dbdeploy.exceptions.DbDeployException;

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

			Class.forName(driver);
			
			new ToPrintSteamDeployer(url, userid, password, new File("."), System.out).doDeploy();

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
