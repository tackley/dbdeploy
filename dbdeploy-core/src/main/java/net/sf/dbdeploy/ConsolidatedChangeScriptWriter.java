package net.sf.dbdeploy;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.scripts.ChangeScript;

import java.io.IOException;
import java.io.PrintStream;

public class ConsolidatedChangeScriptWriter {

	private PrintStream output;

	public ConsolidatedChangeScriptWriter(PrintStream printStream, DbmsSyntax dbmsSyntax) {
		output = printStream;
		output.println(dbmsSyntax.generateScriptHeader());
	}

	public void applyChangeDoScript(ChangeScript script) throws IOException {
		output.println();
		output.println("-- Change script: " + script);
		output.println(script.getContent());
	}

	public void applyChangeUndoScript(ChangeScript script) throws IOException {
		output.println();
		output.println("-- Change script: " + script);
		output.println(script.getUndoContent());
	}

	public void applyDeltaFragmentHeaderOrFooterSql(String sql) {
		output.println();
		output.println(sql);
	}

}
