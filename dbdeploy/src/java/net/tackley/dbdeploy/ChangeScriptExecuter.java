package net.tackley.dbdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import net.tackley.dbdeploy.scripts.ChangeScript;

public class ChangeScriptExecuter {

	private PrintStream output;
	
	public ChangeScriptExecuter(PrintStream printStream) {
		output = printStream;
	}

	public void applyChangeScript(ChangeScript script) throws IOException {
		output.println("\n---\n--- Change script: " + script + "\n---\n");
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

	public void applySqlToSetSchemaVersion(String sql) {
		output.println();
		output.println(sql + "\n/");
		output.println();
	}

}
