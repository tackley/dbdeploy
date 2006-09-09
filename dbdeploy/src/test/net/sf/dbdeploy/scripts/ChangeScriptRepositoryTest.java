package net.sf.dbdeploy.scripts;

import java.util.Arrays;
import java.util.List;

import net.sf.dbdeploy.exceptions.DuplicateChangeScriptException;

import org.jmock.cglib.MockObjectTestCase;

public class ChangeScriptRepositoryTest extends MockObjectTestCase {
	
	public void testGivenASetOfChangeScriptsReturnsThemCorrectly() throws Exception {
		ChangeScript one = new ChangeScript(1);
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript four = new ChangeScript(4);
		
		ChangeScriptRepository repository = new ChangeScriptRepository(Arrays.asList( three, two, four, one ));
		
		List<ChangeScript> list = repository.getOrderedListOfChangeScripts();
		assertThat(4, eq(list.size()));
		assertSame(one, list.get(0));
		assertSame(two, list.get(1));
		assertSame(three, list.get(2));
		assertSame(four, list.get(3));
	}
	
	public void testThrowsWhenConstructedWithAChangeScriptListThatHasDuplicates() throws Exception {
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript anotherTwo = new ChangeScript(2);
		
		try {
			new ChangeScriptRepository(Arrays.asList(three, two, anotherTwo));
			fail("expected exception");
		} catch (DuplicateChangeScriptException ex) {
			assertEquals("There is more than one change script with number 2", ex.getMessage());
		}
	}
}
