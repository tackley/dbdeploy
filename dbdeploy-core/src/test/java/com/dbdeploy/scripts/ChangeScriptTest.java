package com.dbdeploy.scripts;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ChangeScriptTest {
    private static class StringReaderSupplier implements Supplier<Reader> {
        private final String content;

        public StringReaderSupplier(String content) {
            this.content = content;
        }

        public Reader get() throws Exception {
            return new StringReader(content);
        }
    }

	@Test
	public void shouldReturnContentsOfFile() throws Exception {
		final Supplier<Reader> readerSupplier = new StringReaderSupplier("Hello\nThere!\n");

        ChangeScript changeScript = new ChangeScript(5, readerSupplier, "description");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void contentsOfFileShouldExcludeAnythingAfterAnUndoMarker() throws Exception {
		Supplier<Reader> readerSupplier = new StringReaderSupplier(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO\n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, readerSupplier, "description");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void contentsOfFileShouldExcludeAnythingAfterAnUndoMarkerEvenWhenThatMarkerHasSomeWhitespaceAtTheEnd() throws Exception {
		Supplier<Reader> readerSupplier = new StringReaderSupplier(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO   \n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, readerSupplier, "description");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void shouldReturnUndoContentsOfFile() throws Exception {
		Supplier<Reader> readerSupplier = new StringReaderSupplier(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO\n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, readerSupplier, "");
		assertThat(changeScript.getUndoContent(), is("This is after the undo marker!\n"));		
	}

	@Test
	public void changeScriptsNaturallyOrderById() throws Exception {
		ChangeScript one = new ChangeScript(1);
		ChangeScript two = new ChangeScript(2);

		assertThat(one.compareTo(two), lessThan(1));
		assertThat(two.compareTo(one), greaterThanOrEqualTo(1));
	}

	@Test
	public void toStringReturnsASensibleValue() throws Exception {
		ChangeScript changeScript = new ChangeScript(5, null, "abc.txt");
		assertThat(changeScript.toString(), equalTo("#5: abc.txt"));
		
		changeScript = new ChangeScript(5, "abc.txt");
		assertThat(changeScript.toString(), equalTo("#5: abc.txt"));

	}
}
