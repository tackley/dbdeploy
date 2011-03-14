package com.dbdeploy.scripts;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ChangeScriptTest {

	@Test
	public void changeScriptsHaveAnIdAndAFileAndEncoding() throws Exception {
		File file = new File("abc.txt");
		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.getId(), equalTo(5L));
		assertThat(changeScript.getFile(), sameInstance(file));
	}

	@Test
	public void shouldReturnContentsOfFile() throws Exception {
		File file = createTemporaryFileWithContent("Hello\nThere!\n");

		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void contentsOfFileShouldExcludeAnythingAfterAnUndoMarker() throws Exception {
		File file = createTemporaryFileWithContent(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO\n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void contentsOfFileShouldExcludeAnythingAfterAnUndoMarkerEvenWhenThatMarkerHasSomeWhitespaceAtTheEnd() throws Exception {
		File file = createTemporaryFileWithContent(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO   \n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.getContent(), is("Hello\nThere!\n"));
	}

	@Test
	public void shouldReturnUndoContentsOfFile() throws Exception {
		File file = createTemporaryFileWithContent(
				"Hello\n" +
				"There!\n" +
				"--//@UNDO\n" +
				"This is after the undo marker!\n");

		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.getUndoContent(), is("This is after the undo marker!\n"));		
	}

	private File createTemporaryFileWithContent(String content) throws IOException {
		File file = File.createTempFile("changeScriptTest", ".sql");
		file.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.close();
		return file;
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
		File file = new File("abc.txt");
		ChangeScript changeScript = new ChangeScript(5, file, "UTF-8");
		assertThat(changeScript.toString(), equalTo("#5: abc.txt"));
		
		changeScript = new ChangeScript(5, "abc.txt");
		assertThat(changeScript.toString(), equalTo("#5: abc.txt"));

	}
}
