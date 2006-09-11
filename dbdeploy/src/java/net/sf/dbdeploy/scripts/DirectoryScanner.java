package net.sf.dbdeploy.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.dbdeploy.exceptions.UnrecognisedFilenameException;

public class DirectoryScanner {
	
	private FilenameParser filenameParser;
	
	public DirectoryScanner() {
		this(new FilenameParser());
	}
	
	public DirectoryScanner(FilenameParser filenameParser) {
		this.filenameParser = filenameParser;
	}

	public List<ChangeScript> getChangeScriptsForDirectory(File directory)  {
		try {
			System.err.println("Reading change scripts from directory " + directory.getCanonicalPath() + "...");
		} catch (IOException e1) {
			// ignore
		}

		List<ChangeScript> scripts = new ArrayList<ChangeScript>();
		
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			try {
				int id = filenameParser.extractIdFromFilename(filename);
				scripts.add(new ChangeScript(id, file));
			} catch (UnrecognisedFilenameException e) {
				System.err.println("ignoring file " + filename + ": cannot parse filename");
			}
		}
		
		return scripts;

	}

}
