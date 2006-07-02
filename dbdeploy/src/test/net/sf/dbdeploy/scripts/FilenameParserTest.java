package net.sf.dbdeploy.scripts;

import net.sf.dbdeploy.exceptions.UnrecognisedFilenameException;
import net.sf.dbdeploy.scripts.FilenameParser;
import junit.framework.TestCase;

public class FilenameParserTest extends TestCase {

	public void testCanParseAnyFilenameThatStartsWithANumber() throws Exception {
		FilenameParser parser = new FilenameParser();
		assertEquals(1, parser.extractIdFromFilename("0001_a_filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1_a_filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1 a filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1.txt"));
		assertEquals(123, parser.extractIdFromFilename("00123_something.txt"));
	}
	
	public void testThrowsWhenFilenameDoesNotStartWithANumber() throws Exception {
		FilenameParser parser = new FilenameParser();
		try {
		parser.extractIdFromFilename("blah blah blah");
		fail("expected exception");
		} catch (UnrecognisedFilenameException e) {
			assertEquals("Could not extract a change script number from filename: blah blah blah", e.getMessage() );
		}
	}
}
