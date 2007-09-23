package net.sf.dbdeploy.scripts;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.io.File;

public class ChangeScriptTest  {

	@Test
	public void changeScriptsHaveAnIdAndAFile() throws Exception {
		File file = new File("abc.txt");
		ChangeScript changeScript = new ChangeScript(5, file);
        assertThat(changeScript.getId(), equalTo(5));
        assertThat(changeScript.getFile(), sameInstance(file));
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
		ChangeScript changeScript = new ChangeScript(5, file);
		assertThat(changeScript.toString(), equalTo("#5: abc.txt"));
	}
}
