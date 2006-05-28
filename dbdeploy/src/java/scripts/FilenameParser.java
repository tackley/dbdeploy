package net.tackley.dbdeploy.scripts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameParser {
	private final Pattern pattern;

	public FilenameParser() {
		pattern = Pattern.compile("(\\d+).*");
	}

	public int extractIdFromFilename(String filename) throws UnrecognisedFilename {
		Matcher matches = pattern.matcher(filename);
		if (!matches.matches() || matches.groupCount() != 1)
			throw new UnrecognisedFilename("Could not extract a change script number from filename: " + filename);
		
		return Integer.parseInt(matches.group(1));	
	 }

}
