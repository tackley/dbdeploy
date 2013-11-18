package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UnrecognisedFilenameException;
import static org.junit.Assert.*;
import org.junit.Test;

public class FilenameParserTest {

	@Test
	public void canParseAnyFilenameThatStartsWithANumber() throws Exception {
		assertEquals(1L, FilenameParser.extractIdFromFilename("0001_a_filename.txt"));
		assertEquals(1L, FilenameParser.extractIdFromFilename("1_a_filename.txt"));
		assertEquals(1L, FilenameParser.extractIdFromFilename("1 a filename.txt"));
		assertEquals(1L, FilenameParser.extractIdFromFilename("1.txt"));
		assertEquals(123L, FilenameParser.extractIdFromFilename("00123_something.txt"));
	}

	@Test
	public void throwsWhenFilenameDoesNotStartWithANumber() throws Exception {
		try {
		    FilenameParser.extractIdFromFilename("blah blah blah");
		    fail("expected exception");
		} catch (UnrecognisedFilenameException e) {
			assertEquals("Could not extract a change script number from filename: blah blah blah", e.getMessage() );
		}
	}
}
