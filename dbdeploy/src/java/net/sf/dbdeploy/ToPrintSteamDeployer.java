package net.sf.dbdeploy;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.DbmsSyntax;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import net.sf.dbdeploy.scripts.DirectoryScanner;

public class ToPrintSteamDeployer {
	
	private File dir;
	private PrintStream doOutputPrintStream;
	private PrintStream undoOutputPrintStream;
	private final DatabaseSchemaVersionManager schemaManager;
	private final DbmsSyntax dbmsSyntax;

	public ToPrintSteamDeployer(DatabaseSchemaVersionManager schemaManager, File dir, PrintStream outputPrintStream, DbmsSyntax dbmsSyntax, PrintStream undoOutputPrintStream) {
		this.schemaManager = schemaManager;
		this.dir = dir;
		this.doOutputPrintStream = outputPrintStream;
		this.dbmsSyntax = dbmsSyntax;
		this.undoOutputPrintStream = undoOutputPrintStream;
	}

	public void doDeploy(Integer lastChangeToApply) throws SQLException, DbDeployException, IOException {
		System.out.println("dbdeploy v2.01");
		
		ChangeScriptRepository repository = new ChangeScriptRepository(new DirectoryScanner().getChangeScriptsForDirectory(dir));
		ChangeScriptExecuter doScriptExecuter = new ChangeScriptExecuter(doOutputPrintStream, dbmsSyntax);
		Controller doController = new Controller(schemaManager, repository, doScriptExecuter);
		doController.processDoChangeScripts(lastChangeToApply);
		doOutputPrintStream.flush();
		if (undoOutputPrintStream != null) {
			ChangeScriptExecuter undoScriptExecuter = new ChangeScriptExecuter(undoOutputPrintStream, dbmsSyntax);
			Controller undoController = new Controller(schemaManager, repository, undoScriptExecuter);
			undoController.processUndoChangeScripts(lastChangeToApply);
			undoOutputPrintStream.flush();
		}
	}
}
