package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UnrecognisedFilenameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameParser {
	private final Pattern pattern;

	public FilenameParser() {
		pattern = Pattern.compile("(\\d+).*");
	}

	public int extractIdFromFilename(String filename) throws UnrecognisedFilenameException {
		Matcher matches = pattern.matcher(filename);
		if (!matches.matches() || matches.groupCount() != 1)
			throw new UnrecognisedFilenameException("Could not extract a change script number from filename: " + filename);
		
		return Integer.parseInt(matches.group(1));	
	 }

}
