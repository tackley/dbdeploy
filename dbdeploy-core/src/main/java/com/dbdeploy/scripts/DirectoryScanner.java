package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UnrecognisedFilenameException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DirectoryScanner implements Scanner {

	private final String encoding;
    private final File directory;

    public DirectoryScanner(String encoding, File directory) {
        this.encoding = encoding;
        this.directory = directory;
    }
	
	public List<ChangeScript> getChangeScripts()  {
		try {
			System.err.println("Reading change scripts from directory " + directory.getCanonicalPath() + "...");
		} catch (IOException e1) {
			// ignore
		}

		List<ChangeScript> scripts = new ArrayList<ChangeScript>();
		
		for (final File file : directory.listFiles()) {
			if (file.isFile()) {
				String filename = file.getName();
				try {
					long id = FilenameParser.extractIdFromFilename(filename);
					scripts.add(new ChangeScript(id, supplyReader(file), filename));
				} catch (UnrecognisedFilenameException e) {
					// ignore
				}
            }
		}
		
		return scripts;

	}

    private Supplier<Reader> supplyReader(final File file) {
        return new Supplier<Reader>() {
            public Reader get() throws Exception {
                return new InputStreamReader(new FileInputStream(file), encoding);
            }
        };
    }

}
