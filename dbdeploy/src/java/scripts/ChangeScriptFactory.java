package net.tackley.dbdeploy.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChangeScriptFactory {
	
	private FilenameParser filenameParser;
	
	public ChangeScriptFactory(FilenameParser filenameParser) {
		this.filenameParser = filenameParser;
	}

	public ChangeScript[] getChangeScriptsForDirectory(File directory) throws UnrecognisedFilename {
		List<ChangeScript> scripts = new ArrayList<ChangeScript>();
		
		for (File file : directory.listFiles()) {
			String filename = file.getName();
			int id = filenameParser.extractIdFromFilename(filename);
			scripts.add(new ChangeScript(id, file));
		}
		
		return (ChangeScript[]) scripts.toArray();

	}

}
