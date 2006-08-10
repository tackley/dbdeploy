package net.sf.dbdeploy.scripts;

import java.io.File;

import net.sf.dbdeploy.exceptions.DuplicateChangeScriptException;
import net.sf.dbdeploy.exceptions.UnrecognisedFilenameException;

public class ChangeScriptRepositoryFactory {

	private DirectoryScanner directoryScanner;

	public ChangeScriptRepositoryFactory() {
		this(new DirectoryScanner());
	}

	public ChangeScriptRepositoryFactory(DirectoryScanner directoryScanner) {
		this.directoryScanner = directoryScanner;
	}



	public ChangeScriptRepository create(File directory) throws UnrecognisedFilenameException, DuplicateChangeScriptException {
		return new ChangeScriptRepository(directoryScanner.getChangeScriptsForDirectory(directory));
	}

}
