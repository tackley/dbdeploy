package net.tackley.dbdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.tackley.dbdeploy.scripts.ChangeScript;

public class ChangeScriptExecuter {

	public void applyChangeScript(ChangeScript script) throws IOException {
		System.out.println("\n---\n--- Change script: " + script + "\n---\n");
		copyFileContentsToStdOut(script.getFile());
	}

	private void copyFileContentsToStdOut(File file) throws IOException {
       BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        while ((str = in.readLine()) != null) {
            System.out.println(str);
        }
        in.close();
	}

	public void applySqlToSetSchemaVersion(String sql) {
		System.out.println();
		System.out.println("/\n" + sql + "\n/");
		System.out.println();
	}

}
