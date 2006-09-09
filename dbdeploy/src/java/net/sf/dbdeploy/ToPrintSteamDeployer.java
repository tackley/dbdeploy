package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import net.sf.dbdeploy.scripts.DirectoryScanner;

public class ToPrintSteamDeployer {
	
	private String url;
	private String userid;
	private String password;
	private File dir;
	private PrintStream outputPrintStream;

	public ToPrintSteamDeployer(String url, String userid, String password, File dir, PrintStream outputPrintStream) {
		this.url = url;
		this.userid = userid;
		this.password = password;
		this.dir = dir;
		this.outputPrintStream = outputPrintStream;
	}

	public void doDeploy() throws SQLException, DbDeployException, IOException {
		DatabaseSchemaVersionManager databaseSchemaVersion = new DatabaseSchemaVersionManager(url, userid, password);
		ChangeScriptRepository repository = new ChangeScriptRepository(new DirectoryScanner().getChangeScriptsForDirectory(dir));
		ChangeScriptExecuter changeScriptExecuter = new ChangeScriptExecuter(outputPrintStream);
		Controller controller = new Controller(databaseSchemaVersion, repository, changeScriptExecuter);
		controller.applyScriptsToGetChangesUpToLatestVersion(dir);
		outputPrintStream.flush();
	}
}
