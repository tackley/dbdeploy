package net.sf.dbdeploy;

import net.sf.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

public class ToPrintSteamDeployer {
	
	private final PrintStream doOutputPrintStream;
	private final PrintStream undoOutputPrintStream;
	private final DatabaseSchemaVersionManager schemaManager;
	private final DbmsSyntax dbmsSyntax;
	private final ChangeScriptRepository changeScriptRepository;

	public ToPrintSteamDeployer(DatabaseSchemaVersionManager schemaManager, ChangeScriptRepository changeScriptRepository,
	                            PrintStream outputPrintStream, DbmsSyntax dbmsSyntax, PrintStream undoOutputPrintStream) {
		this.schemaManager = schemaManager;
		this.changeScriptRepository = changeScriptRepository;
		this.doOutputPrintStream = outputPrintStream;
		this.dbmsSyntax = dbmsSyntax;
		this.undoOutputPrintStream = undoOutputPrintStream;
	}

	public void doDeploy(Integer lastChangeToApply) throws SQLException, DbDeployException, IOException {
		System.out.println("dbdeploy v2.11");
		
		ConsolidatedChangeScriptWriter doScriptWriter = new ConsolidatedChangeScriptWriter(doOutputPrintStream, dbmsSyntax);
		Controller doController = new Controller(schemaManager, changeScriptRepository, doScriptWriter);
		doController.processDoChangeScripts(lastChangeToApply);
		doOutputPrintStream.flush();

		if (undoOutputPrintStream != null) {
			ConsolidatedChangeScriptWriter undoScriptWriter = new ConsolidatedChangeScriptWriter(undoOutputPrintStream, dbmsSyntax);
			Controller undoController = new Controller(schemaManager, changeScriptRepository, undoScriptWriter);
			undoController.processUndoChangeScripts(lastChangeToApply);
			undoOutputPrintStream.flush();
		}
	}
}
