package net.sf.dbdeploy.scripts;

import net.sf.dbdeploy.exceptions.DuplicateChangeScriptException;
import net.sf.dbdeploy.exceptions.RequiredChangeScriptNotFoundException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;
import junit.framework.TestCase;

public class ChangeScriptRepositoryTest extends TestCase {
	
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
	
	public void testThrowsWhenConstructedWithAChangeScriptListThatHasDuplicates() throws Exception {
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript anotherTwo = new ChangeScript(2);
		
		ChangeScript[] scripts = new ChangeScript[] { three, two, anotherTwo };
		
		try {
			new ChangeScriptRepository(scripts);
			fail("expected exception");
		} catch (DuplicateChangeScriptException ex) {
			assertEquals("There is more than one change script with number 2", ex.getMessage());
		}
		
		
	}
}
