package net.sf.dbdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.dbdeploy.scripts.ChangeScript;

public class ChangeScriptExecuter {

	private PrintStream output;
	
	public ChangeScriptExecuter(PrintStream printStream) {
		output = printStream;
		/* Header data: information and control settings for the entire script. */
		Date now = Calendar.getInstance().getTime();
		output.println("-- Script generated at " + DateFormat.getDateTimeInstance().format(now));
		output.println();
		/* Halt the script on error. */
		output.println("WHENEVER SQLERROR EXIT sql.sqlcode ROLLBACK");
		/* Disable '&' variable substitution. */
		output.println("SET DEFINE OFF");
	}

	public void applyChangeScript(ChangeScript script) throws IOException {
		/* SQL*Plus will treat the C-style comments as documentation and
		 * print it out to the terminal as it executes. */
		output.println();
		output.println("/*");
		output.println("Change script: " + script);
		output.println("*/");
		copyFileContentsToStdOut(script.getFile());
	}

	private void copyFileContentsToStdOut(File file) throws IOException {
       BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        while ((str = in.readLine()) != null) {
        	output.println(str);
        }
        in.close();
	}

	/* Should be called *after* insert of script contents. */
	public void applySqlToSetSchemaVersion(String sql) {
		output.println();
		output.println(sql + ";");
		/* Ensure the schema version update (and any other DML in the current script)
		 * is stored, in case next script fails. */
		output.println("COMMIT;");
	}

}
