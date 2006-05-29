package net.tackley.dbdeploy.scripts;

import net.tackley.dbdeploy.exceptions.RequiredChangeScriptNotFoundException;
import net.tackley.dbdeploy.scripts.ChangeScript;
import junit.framework.TestCase;

public class ChangeScriptRepositoryTest extends TestCase {
//	public void testCanReadFilenames() throws Exception {
//		File file = new File("/home/graham/downloads");
//		System.out.println("CanonicalPath = " + file.getCanonicalPath());
//		System.out.println("path = " + file.getPath());
//		
//		File[] files = file.listFiles();
//		for (File file2 : files) {
//			
//			System.out.println("file = " + file2);
//			System.out.println("filename = " + file2.getName() + " isFile? " + file2.isFile());
//		}
//	}
	
	public void testGivenASetOfChangeScriptsReturnsThemCorrectly() throws Exception {
		ChangeScript one = new ChangeScript(1);
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript four = new ChangeScript(4);
		
		ChangeScript[] scripts = new ChangeScript[] { three, two, four, one };
		ChangeScriptRepository repository = new ChangeScriptRepository(scripts);
		
		assertEquals(4, repository.getHighestAvailableChangeScript());
		assertSame(one, repository.getChangeScript(1));
		assertSame(two, repository.getChangeScript(2));
		assertSame(three, repository.getChangeScript(3));
		assertSame(four, repository.getChangeScript(4));
	}
	
	public void testWorksOkEvenWhenTheChangeScriptsDoNoStartAtOne() throws Exception {
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript four = new ChangeScript(4);
		
		ChangeScript[] scripts = new ChangeScript[] { three, two, four };
		ChangeScriptRepository repository = new ChangeScriptRepository(scripts);
		
		assertEquals(4, repository.getHighestAvailableChangeScript());
		assertSame(two, repository.getChangeScript(2));
		assertSame(three, repository.getChangeScript(3));
		assertSame(four, repository.getChangeScript(4));
	}
	
	public void testThrowsWhenAskingForAChangeScriptThatIsNotFound() throws Exception {
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript four = new ChangeScript(4);
		
		ChangeScript[] scripts = new ChangeScript[] { three, two, four };
		ChangeScriptRepository repository = new ChangeScriptRepository(scripts);
		
		try {
			repository.getChangeScript(5);
			fail("expected exception");
		} catch (RequiredChangeScriptNotFoundException e) {
			assertEquals("Change script #5 was not found and is required", e.getMessage());
		}
				
	}
}
