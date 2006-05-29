package net.tackley.dbdeploy.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tackley.dbdeploy.exceptions.UnrecognisedFilenameException;

public class DirectoryScanner {
	
	private FilenameParser filenameParser;
	
	public DirectoryScanner() {
		this(new FilenameParser());
	}
	
	public DirectoryScanner(FilenameParser filenameParser) {
		this.filenameParser = filenameParser;
	}

	public ChangeScript[] getChangeScriptsForDirectory(File directory) throws UnrecognisedFilenameException {
		List<ChangeScript> scripts = new ArrayList<ChangeScript>();
		
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			int id = filenameParser.extractIdFromFilename(filename);
			scripts.add(new ChangeScript(id, file));
		}
		
		return scripts.toArray(new ChangeScript[scripts.size()]);

	}

}
