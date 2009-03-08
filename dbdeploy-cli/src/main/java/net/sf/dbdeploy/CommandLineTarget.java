package net.sf.dbdeploy;

import net.sf.dbdeploy.exceptions.UsageException;

public class CommandLineTarget {
	private static final DbDeployCommandLineParser commandLineParser = new DbDeployCommandLineParser();

	public static void main(String[] args) {
		try {
			DbDeploy dbDeploy = new DbDeploy();
			commandLineParser.parse(args, dbDeploy);
			dbDeploy.go();
		} catch (UsageException ex) {
			System.err.println("ERROR: " + ex.getMessage());
			commandLineParser.printUsage();
		} catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			ex.printStackTrace();
			System.exit(2);
		}

		System.exit(0);
	}

}
