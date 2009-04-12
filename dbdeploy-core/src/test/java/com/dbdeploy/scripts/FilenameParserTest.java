package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UnrecognisedFilenameException;
import com.dbdeploy.scripts.FilenameParser;
import static org.junit.Assert.*;
import org.junit.Test;

public class FilenameParserTest {

	@Test
	public void canParseAnyFilenameThatStartsWithANumber() throws Exception {
		FilenameParser parser = new FilenameParser();
		assertEquals(1, parser.extractIdFromFilename("0001_a_filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1_a_filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1 a filename.txt"));
		assertEquals(1, parser.extractIdFromFilename("1.txt"));
		assertEquals(123, parser.extractIdFromFilename("00123_something.txt"));
	}

	@Test
	public void throwsWhenFilenameDoesNotStartWithANumber() throws Exception {
		FilenameParser parser = new FilenameParser();
		try {
		parser.extractIdFromFilename("blah blah blah");
		fail("expected exception");
		} catch (UnrecognisedFilenameException e) {
			assertEquals("Could not extract a change script number from filename: blah blah blah", e.getMessage() );
		}
	}
}
