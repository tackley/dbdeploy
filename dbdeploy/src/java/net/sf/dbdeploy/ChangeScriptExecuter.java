package net.sf.dbdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.dbdeploy.database.DbmsSyntax;
import net.sf.dbdeploy.scripts.ChangeScript;

public class ChangeScriptExecuter {

	private PrintStream output;
	
	public ChangeScriptExecuter(PrintStream printStream, DbmsSyntax dbmsSyntax) {
		output = printStream;
		/* Header data: information and control settings for the entire script. */
		Date now = Calendar.getInstance().getTime();
		output.println("-- Script generated at " + DateFormat.getDateTimeInstance().format(now));
		output.println();
		output.println(dbmsSyntax.generateScriptHeader());
	}

	public void applyChangeDoScript(ChangeScript script) throws IOException {
		/* Using double hyphen as comment since ant SQL task doesn't like c-style comments */
		output.println();
		output.println("-- Change script: " + script);
		copyFileDoContentsToStdOut(script.getFile());
	}

	public void applyChangeUndoScript(ChangeScript script) throws IOException {
		/* Using double hyphen as comment since ant SQL task doesn't like c-style comments */
		output.println();
		output.println("-- Change script: " + script);
		copyFileUndoContentsToStdOut(script.getFile());
	}

	private void copyFileDoContentsToStdOut(File file) throws IOException {
       BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        str = in.readLine();
        while ((str != null) && (!str.equals("--//@UNDO"))) {
        	output.println(str);
        	str = in.readLine();
        }
        in.close();
	}

	private void copyFileUndoContentsToStdOut(File file) throws IOException {
       BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        str = in.readLine();
        while ((str != null) && (!str.equals("--//@UNDO"))) {
        	// Just keep looping until we find the magic "--//@UNDO"
        	str = in.readLine();
        }
        str = in.readLine();
        while ((str != null) && (!str.equals("--//@UNDO"))) {
        	output.println(str);
        	str = in.readLine();
        }
        in.close();
	}

	/* Should be called *after* insert of script contents. */
	public void applyDeltaFragmentHeaderOrFooterSql(String sql) {
		output.println();
		output.println(sql);
	}

}
