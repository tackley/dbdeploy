package net.sf.dbdeploy.scripts;

import java.io.File;

import net.sf.dbdeploy.scripts.ChangeScript;


import junit.framework.TestCase;

public class ChangeScriptTest extends TestCase {

	public void testChangeScriptsHaveAnIdAndAFile() throws Exception {
		File file = new File("abc.txt");
		ChangeScript changeScript = new ChangeScript(5, file);
		assertEquals(5, changeScript.getId());
		assertEquals(file, changeScript.getFile());
	}
	
	public void testChangeScriptsNaturallyOrderById() throws Exception {
		ChangeScript one = new ChangeScript(1);
		ChangeScript two = new ChangeScript(2);
		
		assertTrue(one.compareTo(two) < 1);
		assertTrue(two.compareTo(one) >= 1);
	}
	
	public void testToStringReturnsASensibleValue() throws Exception {
		File file = new File("abc.txt");
		ChangeScript changeScript = new ChangeScript(5, file);
		assertEquals("#5: abc.txt", changeScript.toString());
		
	}
}
