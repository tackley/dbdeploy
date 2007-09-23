package net.sf.dbdeploy;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.scripts.ChangeScript;

import java.io.IOException;
import java.io.PrintStream;

public class ConsolidatedChangeScriptWriter {

	private PrintStream output;

	public ConsolidatedChangeScriptWriter(PrintStream printStream, DbmsSyntax dbmsSyntax) {
		output = printStream;
		/* Header data: information and control settings for the entire script. */
		//Date now = Calendar.getInstance().getTime();
		//output.println("-- Script generated at " + DateFormat.getDateTimeInstance().format(now));
		//output.println();
		output.println(dbmsSyntax.generateScriptHeader());
	}

	public void applyChangeDoScript(ChangeScript script) throws IOException {
		/* Using double hyphen as comment since ant SQL task doesn't like c-style comments */
		output.println();
		output.println("-- Change script: " + script);
		output.println(script.getContent());
	}

	public void applyChangeUndoScript(ChangeScript script) throws IOException {
		/* Using double hyphen as comment since ant SQL task doesn't like c-style comments */
		output.println();
		output.println("-- Change script: " + script);
		output.println(script.getUndoContent());
	}

	/* Should be called *after* insert of script contents. */
	public void applyDeltaFragmentHeaderOrFooterSql(String sql) {
		output.println();
		output.println(sql);
	}

}
