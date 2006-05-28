package net.tackley.dbdeploy;

public class CommandLine {

	public static void main(String[] args) {

		try {

			Output output = new Output();

			Controller controller = new Controller(output, null, null, null);
			controller.applyScriptToGetChangesUpToLatestVersion();

		} catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}
}
