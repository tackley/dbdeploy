package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UnrecognisedFilenameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameParser {
	public static final Pattern PATTERN = Pattern.compile("(\\d+).*");

	public static long extractIdFromFilename(String filename) throws UnrecognisedFilenameException {
		Matcher matches = PATTERN.matcher(filename);
		if (!matches.matches() || matches.groupCount() != 1)
			throw new UnrecognisedFilenameException("Could not extract a change script number from filename: " + filename);
		
		return Long.parseLong(matches.group(1));
	 }

}
