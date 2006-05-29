package net.tackley.dbdeploy.scripts;

import java.io.File;

import net.tackley.dbdeploy.exceptions.UnrecognisedFilenameException;

public class ChangeScriptRepositoryFactory {

	private DirectoryScanner directoryScanner;

	public ChangeScriptRepositoryFactory() {
		this(new DirectoryScanner());
	}

	public ChangeScriptRepositoryFactory(DirectoryScanner directoryScanner) {
		this.directoryScanner = directoryScanner;
	}



	public ChangeScriptRepository create(File directory) throws UnrecognisedFilenameException {
		return new ChangeScriptRepository(directoryScanner.getChangeScriptsForDirectory(directory));
	}

}
